package config

import org.gradle.api.Project
import utils.SingleModuleRouterHelper
import utils.kotlinBlock

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

  override fun config() {
    super.config()
    configKtProviderEntry()
  }

  // 把 generateKtProviderEntry 的输出文件路径加到编译里
  private fun configKtProviderEntry() {
    val ktProviderEntryOutputDir = SingleModuleRouterHelper.getKtProviderEntryOutputDir(project)
    project.kotlinBlock {
      sourceSets.named("main") {
        kotlin.srcDir(ktProviderEntryOutputDir)
      }
    }
  }
}