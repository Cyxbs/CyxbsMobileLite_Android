package com.cyxbs.pages.course.page.touch

import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/3 14:36
 */
interface ICourseTouch {

  /**
   * 添加多指触摸分发者
   */
  fun addPointerDispatcher(dispatcher: IPointerDispatcher)

  /**
   * 得到 [pointerId] 对应的 [IPointerTouchHandler]
   */
  fun getTouchHandler(pointerId: Int): IPointerTouchHandler?
}