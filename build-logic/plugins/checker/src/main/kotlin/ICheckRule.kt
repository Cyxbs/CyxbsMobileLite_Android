import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * 2022/12/20 17:39
 */
interface ICheckRule {
  /**
   * 配置时触发
   */
  @kotlin.jvm.Throws(RuntimeException::class)
  fun onConfig(project: Project)
}