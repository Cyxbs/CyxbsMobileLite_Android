package utils

import com.g985892345.provider.plugin.gradle.extensions.KtProviderExtensions
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.dependencies

/**
 * .
 *
 * @author 985892345
 * @date 2024/1/5 16:42
 */
class SingleModuleRouterHelper(
  val project: Project
) {

  fun config() {
    /*
    * 开启了单模块的模块在处理依赖关系时会遇到几个问题：
    * 1. api 实现模块未加入编译，最后打包的 apk 不会存在实现类
    * 2. KtProvider 要求根节点模块才能调用初始化路由的方法，但 Application 却在 singlemodule 模块里面
    *
    * 以下为对应问题的解决方案：
    * 1. 设置依赖 api 模块的监听，然后自动 runtimeOnly 其父模块加入编译
    *   1.1. 但这样会引入一个新的问题，runtimeOnly 无法在编译期找到引用，只能在运行期动态加载路由
    * 2. singlemodule 模块可以在 Application 初始化时反射加载根节点模块的 KtProviderInitializer 实现类
    *    反射的原因是因为 singlemodule 模块不能反相依赖 根节点模块
    * 3. 对于 1.1 的问题可以在编译期收集 runtimeOnly 依赖然后解决
    *
    * */
    runtimeOnlyApiParentModule(project, project)
    createKtProviderEntryTask()
  }

  private val ktProviderEntryOutputDir = getKtProviderEntryOutputDir(project)

  // 创建一个 task 用于在 singlemodule 模块下生成根节点模块路由加载类和所有 runtimeOnly 模块的路由加载类
  private fun createKtProviderEntryTask() {
    val taskProvider = project.tasks.register("generateKtProviderEntry") {
      group = "cyxbs single module"
      // 根节点模块的 KtProviderInitializer 实现类 + 所有 runtimeOnly 模块的 KtProviderInitializer 实现类
      val ktProviderProjects = listOf(project) + getAllRuntimeOnlyProjects()
      inputs.property("ktProviderProjects", ktProviderProjects.map { it.path })
      doFirst {
        // 这里需要所有模块都引入了 KtProvider 都存在 KtProviderInitializer 实现类
        // 如果后续有一个模块不引入的话这里需要单独做黑名单处理
        val ktProviderClassNames = ktProviderProjects.map {
          KtProviderExtensions.getInitializerClass(it)
        }.joinToString(separator = ", \n") { "  \"$it\"" }
        ktProviderEntryOutputDir.get().asFile.apply {
          deleteRecursively()
          mkdirs()
        }.resolve("KtProviderEntries.kt")
          .writeText(
            "// 自动生成，task 为 ${this@doFirst.name} 代码位置在 ${this@SingleModuleRouterHelper::class.simpleName}\n" +
            "internal val ktProviderEntries = arrayOf(\n" + ktProviderClassNames + "\n)"
          )
      }
    }
    project.kotlinBlock {
      sourceSets.named("main") {
        kotlin.srcDir(taskProvider)
      }
    }
  }

  private fun getAllRuntimeOnlyProjects(): Sequence<Project> {
    return project.configurations
      .getByName("runtimeOnly")
      .dependencies
      .asSequence()
      .filterIsInstance<ProjectDependency>()
      .map { it.dependencyProject }
  }

  companion object {
    /**
     * 单模块调试需要反向依赖 api 模块的实现模块，不然因为未加入编译，会因找不到实现类，Router 会报空指针
     *
     * 但存在 单模块A 依赖了 模块B，模块B 依赖了 api 模块C，这种间接依赖 api 模块也要进行处理
     *
     * 所以该方法就是为了进行递归遍历整个依赖树，将所有间接或直接的 api 依赖都同时 runtimeOnly 上该 api 的实现模块
     *
     * 目前 api 模块的实现模块有且只能有父模块一个 !!!
     *
     * @param mainProject 主模块，需要 runtimeOnly 依赖 api 实现模块
     * @param observeProject 需要被观察的模块
     * @param dependedProjects 已经反向依赖了的 Project 集合
     * @param observedProjects 已经进行观察了的 Project 集合
     */
    private fun runtimeOnlyApiParentModule(
      mainProject: Project,
      observeProject: Project,
      dependedProjects: MutableSet<Project> = mutableSetOf(mainProject),
      observedProjects: MutableSet<Project> = mutableSetOf(),
    ) {
      if (observedProjects.contains(observeProject)) {
        // 已经被观察的模块就取消观察
        return
      } else {
        // 没有被观察的模块添加记录，防止重复观察
        observedProjects.add(observeProject)
      }
      observeProject.configurations.all {
        // all 方法是一种观察性的回调，它会把已经添加了的和之后将要添加的都进行回调
        val configuration = this
        val name = configuration.name
        if (name == "api" || name == "implementation" || name == "runtimeOnly") {
          // 只匹配 api、implementation、runtimeOnly
          configuration.dependencies.all {
            val dependency = this
            if (dependency is ProjectDependency) {
              // 如果依赖的是一个 Project
              val dependencyProject = dependency.dependencyProject
              if (!dependedProjects.contains(dependencyProject)) {
                observeDependOtherProject(
                  mainProject,
                  dependencyProject,
                  dependedProjects,
                  observedProjects
                )
              }
            }
          }
        }
      }
    }

    // 观察一个 Project 依赖的其他 Project
    private fun observeDependOtherProject(
      mainProject: Project,
      dependencyProject: Project,
      dependedProjects: MutableSet<Project>,
      observedProjects: MutableSet<Project>,
    ) {
      when {
        dependencyProject.name.startsWith("api-") -> {
          val parentProject = dependencyProject.parent
          if (parentProject == null) {
            println(
              "${dependencyProject.name} 模块不存在父模块，" +
                  "按照规定: api 模块的实现模块有且只能有父模块一个\n" +
                  "如果不遵守将导致单模块出错 !!!"
            )
          } else {
            if (!dependedProjects.contains(parentProject)) {
              // 对于单模块调试，需要反向依赖 api 的实现模块，不然不会加入编译中
              dependedProjects.add(parentProject) // 记录已经依赖，必须先于 dependencies 调用
              println("已反向 runtimeOnly ${parentProject.path}")
              mainProject.dependencies {
                // 将 api 模块的父模块加入编译
                "runtimeOnly"(parentProject)
              }
            }
            // 对于其他模块递归寻找 api 依赖
            runtimeOnlyApiParentModule(
              mainProject,
              parentProject,
              dependedProjects,
              observedProjects
            )
          }
        }

        else -> {
          // 对于其他模块递归寻找 api 依赖
          runtimeOnlyApiParentModule(
            mainProject,
            dependencyProject,
            dependedProjects,
            observedProjects
          )
        }
      }
    }

    fun getKtProviderEntryOutputDir(project: Project): Provider<Directory> {
      return project.project(":cyxbs-components:singlemodule").layout.buildDirectory.dir(
        "generated/ktProviderEntry/${SourceSet.MAIN_SOURCE_SET_NAME}"
      )
    }
  }
}