import org.gradle.kotlin.dsl.dependencies

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/27 15:07
 */
@Suppress("MemberVisibilityCanBePrivate", "ObjectPropertyName", "SpellCheckingInspection")
object Paging {
  // https://developer.android.com/jetpack/androidx/releases/paging
  const val paging_version = "3.2.0"
  
  const val `paging-runtime` = "androidx.paging:paging-runtime:$paging_version"
  const val `paging-rxjava3` = "androidx.paging:paging-rxjava3:$paging_version"
}

fun DependLibraryScope.dependPaging() {
  dependencies {
    "implementation"(Paging.`paging-runtime`)
  }
}

fun DependLibraryScope.dependPagingRx() {
  dependencies {
    "implementation"(Paging.`paging-rxjava3`)
  }
}

