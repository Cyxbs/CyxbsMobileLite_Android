import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/7/20 15:13
 */
object AutoService {
  // 谷歌官方的一种动态加载库 https://github.com/google/auto/tree/main/service
  val version = "1.1.1"
  val `autoServic-core` = "com.google.auto.service:auto-service-annotations:$version"
  val autoService = "com.google.auto.service:auto-service:$version"
}

fun DependLibraryScope.dependAutoService() {
  // kapt 按需引入
  apply(plugin = "org.jetbrains.kotlin.kapt")
  dependencies {
    // 谷歌官方的一种动态加载库 https://github.com/google/auto/tree/main/service
    "compileOnly"(AutoService.`autoServic-core`)
    "kapt"(AutoService.autoService)
  }
}