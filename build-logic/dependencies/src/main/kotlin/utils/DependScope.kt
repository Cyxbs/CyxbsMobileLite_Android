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
  if (isSingleModuleDebugging) {
    dependLibrary(action)
  }
}

fun Project.singleDependModule(action: DependModuleScope.() -> Unit) {
  if (isSingleModuleDebugging) {
    dependModule(action)
  }
}

// 防止污染 Project 作用域
class DependLibraryScope(project: Project): Project by project
class DependModuleScope(project: Project): Project by project

// 是否处于单模块调试中，只有开启了单模块的主模块才有效，其他因为依赖参与的模块会为 false
val Project.isSingleModuleDebugging: Boolean
  get() = extra.has("isSingleModuleDebugging") && extra.get("isSingleModuleDebugging") as Boolean? == true