package com.cyxbs.pages.course.page.item.touch.helper.click

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.item.touch.ITouchItem
import com.cyxbs.pages.course.page.item.touch.ITouchItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs

/**
 * 用于 item 的点击事件，支持多指触摸
 *
 * 写这个类是为了解决多指分发时第二根手指无法触发点击事件
 *
 * @author 985892345
 * 2023/2/22 11:57
 */
class ClickItemHelper(
  private val click: () -> Unit
) : ITouchItemHelper {
  
  fun setClickable(boolean: Boolean) {
    mIsClickable = boolean
  }
  
  fun isClickable(): Boolean {
    return mIsClickable
  }
  
  private var mPointerId = MotionEvent.INVALID_POINTER_ID
  private var mInitialX = 0
  private var mInitialY = 0
  private var mLastMoveX = 0
  private var mLastMoveY = 0
  
  private var mIsClickable = true
  
  private var mIsTriggerClick = true // 是否允许触发点击。如果移动距离过大，则不会触发点击
  
  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
    when (event.action) {
      IPointerEvent.Action.DOWN -> {
        if (mPointerId == MotionEvent.INVALID_POINTER_ID) {
          if (!mIsClickable) return
          mPointerId = event.pointerId
          mInitialX = event.x.toInt() // 重置
          mInitialY = event.y.toInt() // 重置
          mLastMoveX = mInitialX // 重置
          mLastMoveY = mInitialY // 重置
          mIsTriggerClick = true // 重置
        }
      }
      IPointerEvent.Action.MOVE -> {
        if (event.pointerId == mPointerId) {
          if (!mIsClickable) {
            // 存在在事件分发中取消点击事件
            mIsTriggerClick = false
          }
          if (mIsTriggerClick) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            val touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
            val isInTouchSlop = abs(x - mLastMoveX) <= touchSlop && abs(y - mLastMoveY) <= touchSlop
            val isInChild = (x - child.x.toInt()) in 0..child.width &&
              (y - child.y.toInt()) in 0..child.height
            if (!isInTouchSlop || !isInChild) {
              // 移动距离过大就不允许触发长按操作
              mIsTriggerClick = false
            }
          }
        }
      }
      IPointerEvent.Action.UP -> {
        if (event.pointerId == mPointerId) {
          if (mIsTriggerClick && mIsClickable) {
            click.invoke()
          }
          mPointerId = MotionEvent.INVALID_POINTER_ID
        }
      }
      IPointerEvent.Action.CANCEL -> {
        if (event.pointerId == mPointerId) {
          mPointerId = MotionEvent.INVALID_POINTER_ID
        }
      }
    }
  }
}