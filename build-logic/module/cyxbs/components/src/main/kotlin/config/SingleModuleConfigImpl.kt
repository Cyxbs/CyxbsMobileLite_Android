package config

import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/16 22:41
 */
class SingleModuleConfigImpl(project: Project) : CyxbsComponentsConfigImpl(project) {

  override fun getApplicationId(): String {
    throw IllegalStateException("${project.name} 模块不支持单模块调试")
  }
}