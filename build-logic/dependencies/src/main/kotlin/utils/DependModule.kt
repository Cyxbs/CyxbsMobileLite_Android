
import org.gradle.kotlin.dsl.dependencies

/**
 * 需要单独设置模块依赖方法写在这个里面，然后 gradle task 就不会自动生成
 *
 * @author 985892345
 * @date 2023/10/31 09:33
 */

// source 模块单独设置依赖，只需要 runtimeOnly 即可
fun DependModuleScope.runtimeOnlySource() {
  dependencies {
    "runtimeOnly"(project(":cyxbs-functions:source"))
  }
}