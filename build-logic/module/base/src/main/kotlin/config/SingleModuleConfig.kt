package config

import org.gradle.kotlin.dsl.dependencies
import utils.SingleModuleRouterHelper

/**
 * 支持单模块的 config
 *
 * @author 985892345
 * 2023/9/5 23:07
 */
interface SingleModuleConfig : LibraryConfig, ApplicationConfig {

  override fun applicationDependModules() {
    SingleModuleRouterHelper(project).config()

    // debugImplementation 依赖 debug 模块
    project.dependencies {
      "debugImplementation"(project.project(":cyxbs-functions:debug"))
    }
  }
}