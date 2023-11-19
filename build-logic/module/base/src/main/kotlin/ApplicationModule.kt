import config.ApplicationConfig
import org.gradle.kotlin.dsl.apply
import utils.androidApp

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 16:09
 */
object ApplicationModule {

  fun config(config: ApplicationConfig) {
    with(config.project) {
      // 注意⚠️：部分配置在 Android.config() 中设置
      apply(plugin = "com.android.application")

      androidApp {
        defaultConfig {
          applicationId = config.getApplicationId()
          targetSdk = config.targetSdk
          versionCode = config.versionCode
          versionName = config.versionName
        }
      }
    }
    // 其他通用的配置在这里面
    AndroidModule.config(config)
    // 设置 application 模块需要的模块
    config.applicationDependModules()
  }
}