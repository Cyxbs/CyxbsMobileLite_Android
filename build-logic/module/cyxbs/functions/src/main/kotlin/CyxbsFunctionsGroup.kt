import config.CyxbsFunctionsConfigImpl
import config.SingleModuleConfig
import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/18 14:25
 */
object CyxbsFunctionsGroup {

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
      else -> CyxbsFunctionsConfigImpl(project)
    }
  }
}