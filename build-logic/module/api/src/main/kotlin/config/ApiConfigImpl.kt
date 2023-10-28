package config

import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/6 00:13
 */
open class ApiConfigImpl(override val project: Project) : LibraryConfig {

  override fun getNamespace(): String {
    return "com.cyxbs.api.${project.name.substringAfterLast("-")}"
  }
}