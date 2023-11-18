import config.LibraryConfig
import extensions.ModuleConfigExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import utils.androidLib

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 16:10
 */
object LibraryModule {

  fun config(config: LibraryConfig) {
    with(config.project) {
      // 注意⚠️：部分配置在 Android.config() 中设置
      apply(plugin = "com.android.library")

      androidLib {
        // android 闭包
      }
    }
    // 其他通用的配置在这里面
    AndroidModule.config(config)
    // 依赖子模块
    dependChildModule(config)
  }

  /**
   * 依赖子模块，默认依赖所有子模块
   */
  fun dependChildModule(config: LibraryConfig) {
    with(config.project) {
      val moduleConfig = extensions.getByType(ModuleConfigExtension::class.java)
      afterEvaluate {
        // 需要在 afterEvaluate 才能获取到设置的值
        if (moduleConfig.isNeedDependChild) {
          // 自动依赖模块中的子模块
          dependencies {
            subprojects {
              "implementation"(this)
            }
          }
        }
      }
    }
  }
}