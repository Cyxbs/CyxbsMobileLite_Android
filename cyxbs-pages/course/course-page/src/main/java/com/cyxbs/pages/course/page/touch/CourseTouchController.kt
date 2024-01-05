package com.cyxbs.pages.course.page.touch

import android.graphics.PointF
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.controller.CourseController
import com.cyxbs.pages.course.page.item.touch.ITouchItem
import com.cyxbs.pages.course.view.utils.getOrPut
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.abs

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/2 21:19
 */
open class CourseTouchController: CourseController(), ICourseTouch {

  override fun onCreateView(page: ICoursePage) {
    super.onCreateView(page)
    page.course.touch.setDefaultPointerDispatcher(DefaultPointerDispatcher(page))
    page.course.touch.addDispatchPointerTouchHandler(DispatchPointerTouchHandler(page))
  }

  override fun addPointerDispatcher(dispatcher: IPointerDispatcher) {
    page.course.touch.addPointerDispatcher(dispatcher)
  }

  override fun getTouchHandler(pointerId: Int): IPointerTouchHandler? {
    return page.course.touch.getTouchHandler(pointerId)
  }

  /**
   * 传递 dispatchTouchEvent 给 [ITouchItem]
   */
  class DispatchPointerTouchHandler(
    val page: ICoursePage
  ) : IPointerTouchHandler {

    private val mItemByPointerId = SparseArray<Pair<ITouchItem, View>>()

    override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
      val x = event.x.toInt()
      val y = event.y.toInt()
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          // DOWN 事件是手动传递下来的
          val pair = page.course.container.findPairUnderByXY(x, y)
          if (pair != null && pair.first is ITouchItem && pair.second.isVisible) {
            @Suppress("UNCHECKED_CAST")
            mItemByPointerId.put(event.pointerId, pair as Pair<ITouchItem, View>)
            val item = pair.first
            item.touchHelper.onDispatchTouchEvent(event, view, pair.second, item, page)
          }
        }
        IPointerEvent.Action.MOVE -> {
          val pair = mItemByPointerId.get(event.pointerId)
          pair?.first?.touchHelper?.onDispatchTouchEvent(event, view, pair.second, pair.first, page)
        }
        IPointerEvent.Action.UP,
        IPointerEvent.Action.CANCEL -> {
          val pair = mItemByPointerId.get(event.pointerId)
          if (pair != null) {
            pair.first.touchHelper.onDispatchTouchEvent(event, view, pair.second, pair.first, page)
            mItemByPointerId.remove(event.pointerId)
          }
        }
      }
    }
  }

  /**
   * 该类为事件分发优先级最低的默认分发者
   *
   * 为了不拦截子 View 的点击事件，采取了跟 ScrollView 一样的策略，在 MOVE 超过 touchSlop 距离后才进行拦截。
   * 同时，为了保证 [ITouchItem] 能够得到 DOWN 事件，所以手动调用了 onPointerTouchEvent() 方法
   */
  class DefaultPointerDispatcher(
    val page: ICoursePage
  ) : IPointerDispatcher {

    private val mDefaultPointerHandler = DefaultPointerHandler(page)

    private val mLastMovePointByPointerId = SparseArray<PointF>()

    private var mIsAdvanceIntercept = false

    override fun isPrepareToIntercept(event: IPointerEvent, view: ViewGroup): Boolean {
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          // 因为 getInterceptHandler() 在 DOWN 时会返回 null，所以需要手动调用来传递 DOWN 事件
          mDefaultPointerHandler.onPointerTouchEvent(event, view)
          return true
        }
        IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
          // 如果这里能接受到回调，说明 getInterceptHandler() 没有 return handler
          // 因为没有 return，所以 mDefaultPointerHandler 不会收到 onPointerTouchEvent() 回调，需要手动调用
          mDefaultPointerHandler.onPointerTouchEvent(event, view)
        }
        IPointerEvent.Action.MOVE -> {
          // 因为在 DOWN 就 return true，所以 MOVE 是不会回调的
        }
      }
      return false
    }

    override fun getInterceptHandler(event: IPointerEvent, view: ViewGroup): IPointerTouchHandler? {
      val x = event.x
      val y = event.y
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          val point = mLastMovePointByPointerId.getOrPut(event.pointerId) { PointF() }
          point.x = x
          point.y = y
          // DOWN 事件不能返回 handler，因为返回后会直接拦截子 View 事件，导致点击监听失效
          return null
        }
        IPointerEvent.Action.MOVE -> {
          if (mIsAdvanceIntercept) {
            // 如果已经提前拦截，此时就不需要传递事件给子 View，直接返回 handler 即可
            return mDefaultPointerHandler
          }
          mIsAdvanceIntercept = mDefaultPointerHandler.isAdvanceIntercept(event.pointerId)
          if (mIsAdvanceIntercept) {
            return mDefaultPointerHandler
          }
          val point = mLastMovePointByPointerId.get(event.pointerId)
          val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
          val isInTouchSlop = abs(point.x - x) <= touchSlop && abs(point.y - y) <= touchSlop
          if (isInTouchSlop) {
            point.x = x
            point.y = y
            return null
          } else {
            // 超过 touchSlop，可以拦截子 View 事件了
            // 这里返回后就由 AbstractMultiTouchDispatcher 调用 onPointerTouchEvent()
            return mDefaultPointerHandler
          }
        }
        IPointerEvent.Action.UP, IPointerEvent.Action.CANCEL -> {
          // UP 和 CANCEL 是不会回调的
        }
      }
      return null
    }

    override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
      super.onDispatchTouchEvent(event, view)
      if (event.action == MotionEvent.ACTION_DOWN) {
        mIsAdvanceIntercept = false // 重置
      }
    }
  }

  /**
   * 在没有 handler 处理时，会将事件分发给 [ITouchItem] 或者是 [ScrollTouchHandler] 处理
   */
  class DefaultPointerHandler(
    val page: ICoursePage
  ) : IPointerTouchHandler {

    private val mItemByPointerId = SparseArray<Pair<ITouchItem, View>>()

    fun isAdvanceIntercept(pointerId: Int): Boolean {
      val pair = mItemByPointerId.get(pointerId) ?: return false
      return pair.first.touchHelper.isAdvanceIntercept()
    }

    override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
      val x = event.x.toInt()
      val y = event.y.toInt()
      when (event.action) {
        IPointerEvent.Action.DOWN -> {
          // DOWN 事件是手动传递下来的
          val pair = page.course.container.findPairUnderByXY(x, y)
          if (pair != null && pair.first is ITouchItem && pair.second.isVisible) {
            @Suppress("UNCHECKED_CAST")
            mItemByPointerId.put(event.pointerId, pair as Pair<ITouchItem, View>)
            val item = pair.first
            item.touchHelper.onPointerTouchEvent(event, view, pair.second, item, page)
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
        IPointerEvent.Action.MOVE -> {
          val pair = mItemByPointerId.get(event.pointerId)
          if (pair != null) {
            pair.first.touchHelper.onPointerTouchEvent(event, view, pair.second, pair.first, page)
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
        IPointerEvent.Action.UP,
        IPointerEvent.Action.CANCEL -> {
          val pair = mItemByPointerId.get(event.pointerId)
          if (pair != null) {
            pair.first.touchHelper.onPointerTouchEvent(event, view, pair.second, pair.first, page)
            mItemByPointerId.remove(event.pointerId)
          } else {
            ScrollTouchHandler.onPointerTouchEvent(event, view)
          }
        }
      }
    }
  }
}