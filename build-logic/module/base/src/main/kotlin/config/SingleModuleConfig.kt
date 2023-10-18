package config

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.dependencies

/**
 * 支持单模块的 config
 *
 * @author 985892345
 * 2023/9/5 23:07
 */
interface SingleModuleConfig : LibraryConfig, ApplicationConfig {

  /**
   * 单模块依赖配置
   */
  override fun dependModules() {
    // 因为为了隔绝对其他模块的依赖，所以使用第三方模块采用 implementation 去依赖实现模块(singlemodule 模块已经自动被依赖)
    // 为什么不使用 runtimeOnly 呢？因为 runtimeOnly 不会加入编译环境(会编译，但不能找到依赖关系)，只能运行时加载模块启动类
    val singleModuleProject = project.project(":cyxbs-components:singlemodule")
    reverseDependencies(
      singleModuleProject,
      project,
      mutableSetOf(singleModuleProject, project),
      mutableSetOf(singleModuleProject, project)
    )
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
     * @param singleModuleProject 用于依赖其他模块的模块
     * @param project [singleModuleProject] 间接或直接依赖的模块
     * @param dependedProject 已经反向依赖了的 Project 集合
     * @param observedProject 已经进行观察了的 Project 集合
     */
    private fun reverseDependencies(
      singleModuleProject: Project,
      project: Project,
      dependedProject: MutableSet<Project>,
      observedProject: MutableSet<Project>,
    ) {
      if (observedProject.contains(project)) {
        // 已经被观察的模块就取消观察
        return
      } else {
        // 没有被观察的模块添加记录，防止重复观察
        observedProject.add(project)
      }
      project.configurations.all {
        // all 方法是一种观察性的回调，它会把已经添加了的和之后将要添加的都进行回调
        val configuration = this
        if (configuration.name.matches(Regex("(api)|(implementation)|(runtimeOnly)"))) {
          // 只匹配 implementation、api、runtimeOnly
          configuration.dependencies.all {
            val dependency = this
            if (dependency is ProjectDependency) {
              // 如果依赖的是一个 Project
              val dependencyProject = dependency.dependencyProject
              if (!dependedProject.contains(dependencyProject)) {
                observeDependOtherProject(
                  singleModuleProject,
                  dependedProject,
                  observedProject,
                  dependencyProject
                )
              }
            }
          }
        }
      }
    }

    // 观察一个 Project 依赖的其他 Project
    private fun observeDependOtherProject(
      singleModuleProject: Project,
      dependedProject: MutableSet<Project>,
      observedProject: MutableSet<Project>,
      dependencyProject: Project,
    ) {
      when {
        dependencyProject.name.startsWith("api-") -> {
          val parentProject = dependencyProject.parent
          if (parentProject == null) {
            singleModuleProject.logger.warn(
              "${dependencyProject.name} 模块不存在父模块，" +
                  "按照规定: api 模块的实现模块有且只能有父模块一个\n" +
                  "如果不遵守将导致单模块出错 !!!"
            )
          } else {
            // 对于单模块调试，需要反向依赖 api 的实现模块，不然未加入编译，无法生成路由文件
            dependedProject.add(parentProject) // 记录已经依赖，必须先于 dependencies 调用
            singleModuleProject.dependencies {
              // 将 api 模块的父模块加入编译
              // 这里不能使用 runtimeOnly，因为 runtimeOnly 不会触发 kcp 中
              "implementation"(parentProject)
            }
          }
        }

        else -> {
          if (!observedProject.contains(dependencyProject)) {
            // 对于其他模块递归寻找 api 依赖
            reverseDependencies(
              singleModuleProject,
              dependencyProject,
              dependedProject,
              observedProject
            )
          }
        }
      }
    }
  }
}