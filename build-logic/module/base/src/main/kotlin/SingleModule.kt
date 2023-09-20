import config.SingleModuleConfig
import utils.androidApp

/**
 * .
 *
 * @author 985892345
 * 2023/9/15 21:19
 */
object SingleModule {

  fun config(config: SingleModuleConfig) {
    ApplicationModule.config(config)
    LibraryModule.dependChildModule(config)
    with(config.project) {
      androidApp {
        sourceSets {
          getByName("main") {
            // 将 debug 加入编译环境，单模块需要的代码放这里面
            java.srcDir("src/main/debug")
          }
        }
      }
    }
  }
}