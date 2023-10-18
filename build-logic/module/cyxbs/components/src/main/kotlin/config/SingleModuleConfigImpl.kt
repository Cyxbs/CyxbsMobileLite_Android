package config

import SingleModule
import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/16 22:41
 */
class SingleModuleConfigImpl(project: Project) : CyxbsComponentsConfigImpl(project) {
  override fun config() {
    super.config()
    SingleModule.onConfigSingleModuleProject(project)
  }
}