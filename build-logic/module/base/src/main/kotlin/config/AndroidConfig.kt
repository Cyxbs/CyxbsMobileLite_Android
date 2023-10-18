package config

import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 16:53
 */
sealed interface AndroidConfig {

  val project: Project

  val minSdk: Int
    get() = 24

  val compileSdk: Int
    get() = 33

  /**
   * 得到 namespace，即 R 文件的包名
   */
  fun getNamespace(): String

  /**
   * 在应用插件后调用
   */
  fun config() {}

  companion object {
    val resourcesExclude = listOf(
      "LICENSE.txt",
      "META-INF/DEPENDENCIES",
      "META-INF/ASL2.0",
      "META-INF/NOTICE",
      "META-INF/LICENSE",
      "META-INF/LICENSE.txt",
      "META-INF/services/javax.annotation.processing.Processor",
      "META-INF/MANIFEST.MF",
      "META-INF/NOTICE.txt",
      "META-INF/rxjava.properties",
      "**/schemas/**", // 用于取消数据库的导出文件
    )

    val jniExclude = listOf<String>(

    )
  }
}