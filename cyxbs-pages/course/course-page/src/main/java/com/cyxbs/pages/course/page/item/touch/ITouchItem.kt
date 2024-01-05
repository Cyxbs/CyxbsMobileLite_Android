package com.cyxbs.pages.course.page.item.touch

import com.cyxbs.pages.course.page.touch.CourseTouchController
import com.cyxbs.pages.course.view.item.IItem
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher

/**
 * 支持自定义事件处理的 item
 *
 * ## 注意
 * - 该类 item 接收到的是 course 的事件分发
 * - 为了不与 [IPointerDispatcher] 冲突，该事件分发的优先级是最低的，具体实现可查看 [CourseTouchController]
 * - 该 item 的事件分发采取 TouchSlop 范围检测，在该范围内不会拦截子 View 事件 (确保子 View 点击监听不失效)
 *
 * @author 985892345
 * 2023/2/6 13:50
 */
interface ITouchItem : IItem {

  /**
   * 应该重写为一个持久的变量，而不是每次都调用都生成新的对象值
   */
  val touchHelper: ITouchItemHelper
}