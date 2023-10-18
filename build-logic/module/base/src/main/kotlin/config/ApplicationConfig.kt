package config

import org.gradle.api.Project
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
   * 如果你的 applications 子模块依赖有特殊要求，请重新该方法
   */
  fun dependModules() {
    val mainProject = project
    mainProject.project(":cyxbs-components").subprojects {
      when (name) {
        "singlemodule" -> Unit // singlemodule 不依赖
        else -> dependChildModules(mainProject, this)
      }
    }
    mainProject.project(":cyxbs-functions").subprojects {
      val thisProject = this
      when (name) {
        "debug" -> mainProject.dependencies { "debugImplementation"(thisProject) }
        else -> dependChildModules(mainProject, this)
      }
    }
    mainProject.project(":cyxbs-pages").subprojects {
      when (name) {
        else -> dependChildModules(mainProject, this)
      }
    }
  }

  companion object {
    /**
     * 递归依赖 [parentProject] 以及其子模块
     */
    private fun dependChildModules(mainProject: Project, parentProject: Project) {
      if (mainProject != parentProject) {
        mainProject.dependencies {
          "implementation"(parentProject)
        }
      }
      parentProject.subprojects {
        // 递归依赖子模块
        dependChildModules(mainProject, this)
      }
    }
  }
}