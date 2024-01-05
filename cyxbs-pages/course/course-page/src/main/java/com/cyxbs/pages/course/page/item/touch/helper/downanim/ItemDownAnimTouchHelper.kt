package com.cyxbs.pages.course.page.item.touch.helper.downanim

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.item.touch.ITouchItem
import com.cyxbs.pages.course.page.item.touch.ITouchItemHelper
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.pow

/**
 * ## 点击 View 实现 Q 弹动画的事件帮助类
 *
 * @author 985892345
 * @date 2023/12/3 16:29
 */
open class ItemDownAnimTouchHelper : ITouchItemHelper {

  override fun onPointerTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) = Unit

  private var mInitialX = Int.MIN_VALUE
  private var mInitialY = Int.MIN_VALUE

  private var mPointerId = MotionEvent.INVALID_POINTER_ID

  override fun onDispatchTouchEvent(
    event: IPointerEvent,
    parent: ViewGroup,
    child: View,
    item: ITouchItem,
    page: ICoursePage
  ) {
    super.onDispatchTouchEvent(event, parent, child, item, page)
    val x = event.x.toInt()
    val y = event.y.toInt()
    when (event.action) {
      IPointerEvent.Action.DOWN -> {
        if (mPointerId != MotionEvent.INVALID_POINTER_ID) {
          // 存在多根手指触摸同个 item，直接结束动画
          tryEndAnim(child, x, y)
        } else {
          mPointerId = event.pointerId
          mInitialX = x
          mInitialY = y
          startAnim(child, x, y)
        }
      }
      IPointerEvent.Action.MOVE -> {
        if (event.pointerId == mPointerId && mInitialX != Int.MIN_VALUE) {
          if (child.translationX != 0F || child.translationY != 0F) {
            tryEndAnim(child, x, y)
          } else {
            val l = child.left
            val r = l + child.width
            val t = child.top
            val b = t + child.height
            if (x !in l..r || y !in t..b) {
              // 移动到 View 外面就取消动画
              tryEndAnim(child, x, y)
            } else {
              changeView(child, mInitialX, mInitialY, x, y)
            }
          }
        }
      }
      IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
        if (event.pointerId == mPointerId) {
          tryEndAnim(child, x, y)
        }
        if (event.event.pointerCount == 0) {
          // 只有全部手指都离开时才重置
          mPointerId = MotionEvent.INVALID_POINTER_ID
        }
      }
    }
  }

  private fun tryEndAnim(view: View, x: Int, y: Int) {
    if (mInitialX != Int.MIN_VALUE) {
      endAnim(view, mInitialX, mInitialY, x, y)
      mInitialX = Int.MIN_VALUE
      mInitialY = Int.MIN_VALUE
    }
  }

  protected open fun startAnim(view: View, initialX: Int, initialY: Int) {
    view.animate()
      .scaleX(0.85F)
      .scaleY(0.85F)
      .setInterpolator { 1 - 1F / (1F + it).pow(6) }
      .start()
  }

  protected open fun changeView(view: View, initialX: Int, initialY: Int, nowX: Int, nowY: Int) {
    val centerY = view.height / 2F
    val centerX = view.width / 2F
    view.rotationX = -(nowY - view.y - centerY) / centerY * ((-0.0023F * view.height + 1.7F) * 16) // 上下翻转
    view.rotationY = (nowX - view.x - centerX) / centerX * ((-0.0023F * view.width + 1.7F) * 10) // 左右翻转
  }

  protected open fun endAnim(view: View, initialX: Int, initialY: Int, nowX: Int, nowY: Int) {
    view.animate()
      .scaleX(1F)
      .scaleY(1F)
      .rotationX(0F)
      .rotationY(0F)
      .setInterpolator(OvershootInterpolator())
      .start()
  }
}