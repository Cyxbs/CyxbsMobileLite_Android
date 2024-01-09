import config.CyxbsComponentsConfigImpl
import config.SingleModuleConfig
import config.SingleModuleConfigImpl
import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 23:34
 */
object CyxbsComponentsGroup {

  fun config(project: Project, isSingleModule: Boolean = false) {
    val config = createConfig(project)
    if (isSingleModule) {
      SingleModule.config(config)
    } else {
      LibraryModule.config(config)
    }
    config.config()
  }

  private fun createConfig(project: Project): SingleModuleConfig {
    return when (project.name) {
      "singlemodule" -> SingleModuleConfigImpl(project)
      else -> CyxbsComponentsConfigImpl(project)
    }
  }
}