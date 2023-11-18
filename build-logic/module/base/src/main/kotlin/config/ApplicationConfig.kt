package config

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 17:09
 */
interface ApplicationConfig : AndroidConfig {

  val targetSdk: Int
    get() = compileSdk

  val versionCode: Int
    get() = 1

  val versionName: String
    get() = "1.0.0"

  fun getApplicationId(): String

  /**
   * 设置 application 模块的依赖
   *
   * 如果你的 applications 子模块依赖有特殊要求，请重写该方法
   */
  fun applicationDependModules() {
    dependChildModules(
      project,
      project.project(":cyxbs-components"),
      mapOf(
        "singlemodule" to { }, // singlemodule 不依赖
      )
    )
    dependChildModules(
      project,
      project.project(":cyxbs-functions"),
      mapOf(
        "debug" to { "debugImplementation"(it) },
      )
    )
    dependChildModules(
      project,
      project.project(":cyxbs-pages"),
      mapOf(
      )
    )
  }

  companion object {
    /**
     * 递归依赖 [thisProject] 以及其子模块
     * @param exclude 排除依赖
     */
    fun dependChildModules(
      mainProject: Project,
      thisProject: Project,
      exclude: Map<String, DependencyHandlerScope.(Project) -> Unit>,
    ) {
      mainProject.dependencies {
        val action = exclude[thisProject.name]
        if (action == null) {
          if (thisProject.projectDir.resolve("build.gradle.kts").exists()) {
            "implementation"(thisProject)
          }
        } else {
          action.invoke(this, thisProject)
        }
      }
      thisProject.subprojects {
        // 递归依赖子模块
        dependChildModules(mainProject, this, exclude)
      }
    }
  }
}