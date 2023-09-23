package config

import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/18 14:26
 */
class CyxbsPagesConfigImpl(override val project: Project) : SingleModuleConfig {

  override fun getNamespace(): String {
    return "com.cyxbs.pages.${project.name}"
  }

  override fun getApplicationId(): String {
    return "com.cyxbs.pages.${project.name}"
  }
}