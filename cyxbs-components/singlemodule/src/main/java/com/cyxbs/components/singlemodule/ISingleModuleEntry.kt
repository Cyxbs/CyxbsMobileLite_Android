package com.cyxbs.components.singlemodule

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * 负责单模块调试的入口界面启动
 *
 * @author 985892345
 * @date 2023/9/7 00:08
 */
interface ISingleModuleEntry {

  /**
   * 返回一个 Intent 或者 Fragment 对象，返回其他对象则抛出异常
   */
  fun getPage(context: Context): Page

  /**
   * 是否锁定竖屏
   */
  val isPortraitScreen: Boolean
    get() = true

  /**
   * 是否沉浸式状态栏
   */
  val isCancelStatusBar: Boolean
    get() = true

  sealed interface Page
  class ActivityPage(val intent: Intent): Page
  class FragmentPage(val fragment: () -> Fragment): Page
}