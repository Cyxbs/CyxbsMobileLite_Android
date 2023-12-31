package config

import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 23:29
 */
open class CyxbsLiteConfigImpl(override val project: Project) : ApplicationConfig {

  override fun getApplicationId(): String {
    return "com.cyxbs.applications.${project.name}"
  }

  override fun getNamespace(): String {
    return "com.cyxbs.applications.${project.name}"
  }
}