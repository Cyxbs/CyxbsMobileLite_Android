import config.LibraryConfig
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
   * 依赖子模块
   */
  fun dependChildModule(config: LibraryConfig) {
    with(config.project) {
      if (config.isNeedDependChild) {
        // 自动依赖模块中的 api 模块
        dependencies {
          // 根 gradle 中包含的所有子模块
          val includeProjects = rootProject.subprojects.map { it.name }

          projectDir.listFiles()!!.filter {
            // 1.是文件夹
            // 2.以 api- 开头
            // 3.根 gradle 导入了的模块
            it.isDirectory
                && "(api-.+)".toRegex().matches(it.name)
                && includeProjects.contains(it.name)
          }.forEach {
            "implementation"(project("${path}:${it.name}"))
          }
        }
      }
    }
  }
}