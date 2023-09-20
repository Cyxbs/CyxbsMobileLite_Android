package config

/**
 * .
 *
 * @author 985892345
 * 2023/9/5 17:09
 */
interface LibraryConfig : AndroidConfig {

  /**
   * 是否需要依赖子模块
   */
  val isNeedDependChild: Boolean
    get() = true
}