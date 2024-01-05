package com.cyxbs.pages.course.view.course.controller

import com.cyxbs.pages.course.view.course.CourseViewGroup
import com.cyxbs.pages.course.view.touch.IMultiTouch
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.MultiTouchDispatcherHelper

/**
 * 实现多指触摸的父类
 *
 * @author 985892345
 * @date 2023/11/30 22:59
 */
class MultiTouchController(
  val course: CourseViewGroup
) : IMultiTouch {

  private val mMultiTouchDispatcherHelper = MultiTouchDispatcherHelper()

  init {
    course.addItemTouchListener(mMultiTouchDispatcherHelper)
  }

  override fun addPointerDispatcher(dispatcher: IPointerDispatcher) {
    mMultiTouchDispatcherHelper.addPointerDispatcher(dispatcher)
  }

  override fun setDefaultPointerDispatcher(dispatcher: IPointerDispatcher?) {
    mMultiTouchDispatcherHelper.setDefaultPointerDispatcher(dispatcher)
  }

  override fun getDefaultPointerDispatcher(): IPointerDispatcher? {
    return mMultiTouchDispatcherHelper.getDefaultPointerDispatcher()
  }

  override fun getTouchHandler(pointerId: Int): IPointerTouchHandler? {
    return mMultiTouchDispatcherHelper.getTouchHandler(pointerId)
  }

  override fun addDispatchPointerTouchHandler(handler: IPointerTouchHandler) {
    mMultiTouchDispatcherHelper.addDispatchPointerTouchHandler(handler)
  }

  override fun removeDispatchPointerTouchHandler(handler: IPointerTouchHandler) {
    mMultiTouchDispatcherHelper.removeDispatchPointerTouchHandler(handler)
  }
}