import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

// 不添加包名，这样 build.gradle.kts 就不用导包

/**
 * ```
 * // 在你的模块里面
 * dependLibrary {
 *   dependCoroutines()
 *   dependGlide()
 *   dependNetwork()
 *   // 其他依赖输入 depend 看 idea 智能提示
 * }
 * ```
 *
 * @author 985892345
 * 2023/9/15 22:34
 */
fun Project.dependLibrary(action: DependLibraryScope.() -> Unit) {
  DependLibraryScope(project).action()
}

fun Project.dependModule(action: DependModuleScope.() -> Unit) {
  DependModuleScope(project).action()
}

// 单模块时进行依赖
fun Project.singleDependLibrary(action: DependLibraryScope.() -> Unit) {
  if (isSingleModule) {
    dependLibrary(action)
  }
}

fun Project.singleDependModule(action: DependModuleScope.() -> Unit) {
  if (isSingleModule) {
    dependModule(action)
  }
}

// 防止污染 Project 作用域
class DependLibraryScope(project: Project): Project by project
class DependModuleScope(project: Project): Project by project

val Project.isSingleModule: Boolean
  get() = extra.has("isSingleModule") && extra.get("isSingleModule") as Boolean? == true