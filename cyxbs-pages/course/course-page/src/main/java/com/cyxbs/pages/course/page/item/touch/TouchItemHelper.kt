package com.cyxbs.pages.course.page.item.touch

import android.view.View
import android.view.ViewGroup
import com.cyxbs.pages.course.page.ICoursePage
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * 用于组合多个 [ITouchItemHelper]
 *
 * @author 985892345
 * 2023/2/6 15:29
 */
class TouchItemHelper(
  private vararg val helpers: ITouchItemHelper
) : ITouchItemHelper {

  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
    repeat(helpers.size) {
      val helper = helpers[it]
      helper.onPointerTouchEvent(event, parent, child, item, page)
    }
  }

  override fun onDispatchTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
    super.onDispatchTouchEvent(event, parent, child, item, page)
    repeat(helpers.size) {
      val helper = helpers[it]
      helper.onDispatchTouchEvent(event, parent, child, item, page)
    }
  }
  
  override fun isAdvanceIntercept(): Boolean {
    repeat(helpers.size) {
      val helper = helpers[it]
      if (helper.isAdvanceIntercept()) {
        return true
      }
    }
    return false
  }
}