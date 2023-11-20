package config

import com.g985892345.provider.plugin.gradle.extensions.KtProviderExtensions
import com.g985892345.provider.plugin.gradle.generator.KtProviderInitializerGenerator
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.dependencies
import utils.kotlinBlock

/**
 * 支持单模块的 config
 *
 * @author 985892345
 * 2023/9/5 23:07
 */
interface SingleModuleConfig : LibraryConfig, ApplicationConfig {

  override fun applicationDependModules() {
    // 单模块需要依赖 api 模块的实现模块，这里分为三个步骤
    // 1、查找所有直接或间接依赖的 api 模块，使用 runtimeOnly 让其父模块加入编译
    // 2、创建一个 task 用于编译时生成所有父模块 KtProviderInitializer 实现类的类全称
    // 3、singlemodule 模块在 Application 初始化时反射加载父模块的 KtProviderInitializer 实现类
    runtimeOnlyApiParentModule(project, project)
    createApiParentKtProviderTask()

    // debugImplementation 依赖 debug 模块
    project.dependencies {
      "debugImplementation"(project.project(":cyxbs-functions:debug"))
    }
  }

  private fun createApiParentKtProviderTask() {
    val taskProvider = project.tasks.register("generateApiParentKtProvider") {
      val apiProjects = mutableSetOf<Project>()
      getAllApiProject(project, apiProjects)
      val apiParentProjects = apiProjects.mapNotNull { it.parent }
      val outputDir = project.layout.buildDirectory.dir(
        "generated/ApiParentKtProvider/${SourceSet.MAIN_SOURCE_SET_NAME}"
      )
      inputs.property("apiParentProjects", apiParentProjects.map { it.path })
      outputs.dir(outputDir)
      dependsOn.add(project.tasks.findByPath(KtProviderInitializerGenerator.getTaskName(project)))
      doFirst {
        val ktProviderClassNames = apiParentProjects.mapNotNull {
          if (it.extensions.findByType(KtProviderExtensions::class.java) != null) {
            KtProviderExtensions.getInitializerClass(it)
          } else null
        }.joinToString { "\"$it\"" }
        outputDir.get().asFile.apply {
          deleteRecursively()
          mkdirs()
        }.resolve("ApiParentKtProviderEntryClassName.kt")
          .writeText(
            """
              // 自动生成，代码逻辑在 SingleModuleConfig#createApiParentKtProviderTask
              
              import com.g985892345.provider.annotation.ImplProvider
              import com.cyxbs.components.singlemodule.internal.IApiParentKtProvider
              
              @ImplProvider
              object ApiParentKtProviderImpl : IApiParentKtProvider {
                override val entryClassNames = listOf($ktProviderClassNames)
              }
            """.trimIndent()
          )
        // 因为单模块自身的 KtProvider 已被初始化，所以这里直接生成一个 singlemodule 模块中实现类
      }
    }
    project.kotlinBlock {
      sourceSets.getByName("main")
        .kotlin
        .srcDir(taskProvider)
    }
  }

  private fun getAllApiProject(
    project: Project,
    apiProjects: MutableSet<Project>,
    observedProject: MutableSet<Project> = mutableSetOf() // 已经被观察的模块，剪枝操作
  ) {
    if (observedProject.contains(project)) return
    observedProject.add(project)
    project.configurations.asSequence().filter {
      it.name == "api" || it.name == "implementation" || it.name == "runtimeOnly"
    }.map { configuration ->
      configuration.dependencies.filterIsInstance<ProjectDependency>()
        .map { it.dependencyProject }
    }.flatten().forEach {
      getAllApiProject(it, apiProjects, observedProject)
    }
    if (project.name.startsWith("api-")) {
      apiProjects.add(project)
    }
  }

  companion object {
    /**
     * 单模块调试需要反向依赖 api 模块的实现模块，不然因为未加入编译，会无法生成路由文件，Router 会报空指针
     *
     * 但存在 单模块A 依赖了 模块B，模块B 依赖了 api 模块C，这种间接依赖 api 模块也要进行处理
     *
     * 所以该方法就是为了进行递归遍历整个依赖树，将所有间接或直接的 api 依赖都同时依赖上该 api 的实现模块
     *
     * 目前 api 模块的实现模块有且只能有父模块一个 !!!
     *
     * @param selfProject 间接或直接依赖的模块
     * @param observeProject 需要被观察的模块
     * @param dependedProjects 已经反向依赖了的 Project 集合
     * @param observedProjects 已经进行观察了的 Project 集合
     */
    private fun runtimeOnlyApiParentModule(
      selfProject: Project,
      observeProject: Project,
      dependedProjects: MutableSet<Project> = mutableSetOf(selfProject),
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
                  selfProject,
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
      selfProject: Project,
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
              selfProject.dependencies {
                // 将 api 模块的父模块加入编译
                "runtimeOnly"(parentProject)
              }
            }
            // 对于其他模块递归寻找 api 依赖
            runtimeOnlyApiParentModule(
              selfProject,
              parentProject,
              dependedProjects,
              observedProjects
            )
          }
        }

        else -> {
          // 对于其他模块递归寻找 api 依赖
          runtimeOnlyApiParentModule(
            selfProject,
            dependencyProject,
            dependedProjects,
            observedProjects
          )
        }
      }
    }
  }
}