import config.SingleModuleConfig
import org.gradle.kotlin.dsl.extra
import utils.androidApp

/**
 * .
 *
 * @author 985892345
 * 2023/9/15 21:19
 */
object SingleModule {

  fun config(config: SingleModuleConfig) {
    // 使用 Project.isSingleModuleDebugging 进行判断
    config.project.extra.set("isSingleModuleDebugging", true)
    ApplicationModule.config(config)
    LibraryModule.dependChildModule(config)
    with(config.project) {
      androidApp {
        sourceSets {
          named("main") {
            // 将 single 加入编译环境，单模块需要的代码放这里面
            java.srcDir("src/main/single")
            res.srcDir("src/main/single-res")
          }
        }
        defaultConfig {
          // 设置单模块安装包名字
          manifestPlaceholders["single_module_app_name"] = "cyxbs.${project.name}"
        }
      }
      dependModule {
        dependSinglemodule()
      }
    }
  }
}