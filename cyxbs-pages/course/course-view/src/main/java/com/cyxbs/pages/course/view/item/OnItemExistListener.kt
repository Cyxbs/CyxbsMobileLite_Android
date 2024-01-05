package com.cyxbs.pages.course.view.item

import android.view.View
import com.ndhzs.netlayout.child.OnChildExistListener

/**
 * 监听正常使用 [IItemContainer.addItem] 时的回调，
 * 如果该 item 被拦截后再次 [IItemContainer.addItem]，则此时不会回调，
 * 但想实现监听 View 被彻底添加进父布局，可以设置 [OnChildExistListener]
 *
 * @author 985892345
 * @date 2023/11/30 23:18
 */
interface OnItemExistListener {

  /**
   * 调用 addView() 前回调
   */
  fun onItemAddedBefore(item: IItem) {}

  /**
   * 调用 addView() 后回调
   *
   * ## 注意
   * - view 如果被 [IItemInterceptor] 拦截，则为 null
   */
  fun onItemAddedAfter(item: IItem, view: View?) {}

  /**
   * 调用 removeView() 前回调，此时可用于设置退场动画
   *
   * ## 注意
   * - view 如果被 [IItemInterceptor] 拦截，则为 null
   */
  fun onItemRemovedBefore(item: IItem, view: View?) {}

  /**
   * 调用 removeView() 后回调
   *
   * ## 注意
   * - view 如果被 [IItemInterceptor] 拦截，则为 null
   */
  fun onItemRemovedAfter(item: IItem, view: View?) {}
}