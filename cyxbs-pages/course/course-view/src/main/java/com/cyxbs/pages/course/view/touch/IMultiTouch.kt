package com.cyxbs.pages.course.view.touch

import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler

/**
 * 添加多指触摸分发的接口
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 12:51
 */
interface IMultiTouch {
  
  /**
   * 添加多指触摸分发者
   */
  fun addPointerDispatcher(dispatcher: IPointerDispatcher)
  
  /**
   * 设置默认的多指触摸分发者，该分发者事件分发的优先级最低，可以用来做一些特殊操作
   */
  fun setDefaultPointerDispatcher(dispatcher: IPointerDispatcher?)
  
  /**
   * 得到默认的多指触摸分发者
   */
  fun getDefaultPointerDispatcher(): IPointerDispatcher?
  
  /**
   * 得到 [pointerId] 对应的 [IPointerTouchHandler]
   */
  fun getTouchHandler(pointerId: Int): IPointerTouchHandler?

  /**
   * 只会在 dispatchTouchEvent 中分发事件的 [IPointerTouchHandler]，会回调所有手指的事件
   */
  fun addDispatchPointerTouchHandler(handler: IPointerTouchHandler)

  /**
   * 移除 [addDispatchPointerTouchHandler] 添加的 handler
   */
  fun removeDispatchPointerTouchHandler(handler: IPointerTouchHandler)
  
}