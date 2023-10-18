import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:41
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Glide {
  // https://github.com/bumptech/glide
  const val glide_version = "4.15.1"
  
  const val glide = "com.github.bumptech.glide:glide:$glide_version"
}

fun DependLibraryScope.dependGlide() {
  dependencies {
    "implementation"(Glide.glide)
  }
}