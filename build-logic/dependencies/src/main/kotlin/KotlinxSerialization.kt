import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * 序列化工具
 *
 * 使用教程: https://juejin.cn/post/7079229035254906888?searchId=2023101811235015EB2120BC7E7F821358
 *
 * @author 985892345
 * @date 2023/10/18 20:09
 */
object KotlinxSerialization {
  const val version = "1.6.0"

  // 官网: https://github.com/Kotlin/kotlinx.serialization
  // 教程: https://juejin.cn/post/7079229035254906888?searchId=2023101811235015EB2120BC7E7F821358
  const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
}

// 序列化 json，本项目不使用 Gson，用些新东西
fun DependLibraryScope.dependSerializationJson() {
  apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
  dependencies {
    "implementation"(KotlinxSerialization.json)
  }
}