package com.cyxbs.components.base.ui

import androidx.annotation.LayoutRes
import com.g985892345.android.base.ui.page.BaseFragment

/**
 * 注释往父类 [BaseFragment] 查看
 *
 * @author 985892345
 * @date 2023/9/6 23:55
 */
abstract class CyxbsBaseFragment : BaseFragment {

  constructor() : super()

  /**
   * 正确用法：
   * ```
   * class TestFragment : BaseFragment(R.layout.test)
   * ```
   *
   * # 禁止使用下面这种写法！！！
   * ```
   * class TestFragment(layoutId: Int) : BaseFragment(layoutId)
   *
   * 这是错误写法！！！
   * ```
   */
  constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)


}