
/**
 * .
 *
 * @author 985892345
 * @date 2023/11/30 21:54
 */
enum class DependType(val value: String) {
  Api("api"),
  RuntimeOnly("runtimeOnly"),
  CompileOnly("compileOnly"),
  Implementation("implementation"),
}