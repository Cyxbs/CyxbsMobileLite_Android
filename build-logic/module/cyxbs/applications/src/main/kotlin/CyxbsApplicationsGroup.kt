import config.ApplicationConfig
import config.CyxbsLiteConfigImpl
import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 23:30
 */
object CyxbsApplicationsGroup {

  fun config(project: Project) {
    val config: ApplicationConfig = when (project.name) {
      "lite" -> CyxbsLiteConfigImpl(project)
      else -> error("未知 cyxbs-applications 子模块，请实现自己的 ApplicationConfig 接口")
    }
    ApplicationModule.config(config)
  }
}