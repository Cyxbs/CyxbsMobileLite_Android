package extensions

import org.gradle.api.Project

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/18 16:16
 */
abstract class ModuleConfigExtension(val project: Project) {

  /**
   * 是否需要依赖子模块，只有单模块和 library 模块才有效
   */
  var isNeedDependChild: Boolean = true
}