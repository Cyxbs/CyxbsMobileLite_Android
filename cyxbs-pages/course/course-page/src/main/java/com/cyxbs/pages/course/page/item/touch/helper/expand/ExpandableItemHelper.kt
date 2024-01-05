package com.cyxbs.pages.course.page.item.touch.helper.expand

import android.graphics.Canvas
import android.view.View
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.item.touch.ITouchItem
import com.cyxbs.pages.course.page.item.touch.helper.longpress.AbstractLongPressItemHelper
import com.cyxbs.pages.course.page.utils.VibratorUtil
import com.cyxbs.pages.course.view.course.ICourseScrollControl
import com.cyxbs.pages.course.view.utils.forEachReversed
import com.cyxbs.pages.course.page.item.touch.helper.longpress.ILongPressItemListener
import com.ndhzs.netlayout.draw.ItemDecoration
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import kotlin.math.min

/**
 * 长按 Item 边缘实现扩大或缩小的帮助类，但具体细节需要你自己实现
 *
 * ## 注意
 * - 如果你需要重写默认的扩展逻辑，请传入你自己的 [config]
 *
 * 因为只需要处理一个长按的手指，所以继承于 [AbstractLongPressItemHelper]
 *
 * @author 985892345
 * 2023/4/19 10:35
 */
class ExpandableItemHelper(
  val config: IExpandableItemConfig = IExpandableItemConfig
) : AbstractLongPressItemHelper(config) {

  /**
   * 添加扩展监听
   */
  fun addExpandableListener(l: IExpandableItemListener) {
    mExpandableItemListeners.add(l)
  }

  fun removeExpandableListener(l: IExpandableItemListener) {
    mExpandableItemListeners.remove(l)
  }

  // ExpandableHandler 不能放进 mExpandableItemListeners 中，因为 mExpandableItemListeners 是倒序遍历的
  // ExpandableHandler 需要保证第一个回调
  private val mExpandableHandler = config.getExpandableHandler()
  private val mExpandableItemListeners = arrayListOf<IExpandableItemListener>()
  private val mLongPressItemListener = LongPressExpandableItemListener()

  init {
    setLongPressItemListener(mLongPressItemListener)
  }

  private inner class LongPressExpandableItemListener : ILongPressItemListener {

    private var mIsLockedNoon = false // 是否锁定了中午时间段
    private var mIsLockedDusk = false // 是否锁定了中午时间段

    private var mIsMoving = false // 控制 move 的调用

    override fun onDown(page: ICoursePage, item: ITouchItem, child: View, event: IPointerEvent) {
      mIsLockedNoon = false // 重置
      mIsLockedDusk = false // 重置
      mIsMoving = false // 重置
      mExpandableHandler.onDown(page, item, child, event)
      mExpandableItemListeners.forEachReversed {
        it.onDown(page, item, child, event)
      }
    }

    override fun onLongPressed(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      x: Int,
      y: Int,
      pointerId: Int
    ) {
      val course = page.course

      VibratorUtil.start(60) // 长按被触发来个震动提醒
      course.view.parent.requestDisallowInterceptTouchEvent(true) // 禁止父布局拦截

      unfoldNoonDuskIfNeed()

      course.scroll.addOnScrollYChanged(mScrollYChangedListener)
      course.view.addOnLayoutChangeListener(mOnLayoutChanged)
      course.addItemDecoration(mItemDecoration)

      mExpandableHandler.onLongPressed(page, item, child, x, y, pointerId)
      mExpandableItemListeners.forEachReversed {
        it.onLongPressed(page, item, child, x, y, pointerId)
      }
    }

    override fun onLongPressCancel(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      event: IPointerEvent
    ) {
      mExpandableHandler.onLongPressCancel(page, item, child, event)
      mExpandableItemListeners.forEachReversed {
        it.onLongPressCancel(page, item, child, event)
      }
    }

    override fun onMove(page: ICoursePage, item: ITouchItem, child: View, x: Int, y: Int) {
      tryPostMove()
    }

    override fun onEventEnd(
      page: ICoursePage,
      item: ITouchItem,
      child: View,
      event: IPointerEvent,
      isInLongPress: Boolean?,
      isCancel: Boolean
    ) {
      val course = page.course
      if (isInLongPress == true) {
        // 激活了长按后抬手或者被 CANCEL
        if (mIsLockedNoon) {
          page.fold.unlockFoldNoon()
        }
        if (mIsLockedDusk) {
          page.fold.unlockFoldDusk()
        }
        course.scroll.removeOnScrollYChanged(mScrollYChangedListener)
        course.view.removeOnLayoutChangeListener(mOnLayoutChanged)
        course.removeItemDecoration(mItemDecoration)
      }
      mExpandableHandler.onEventEnd(page, item, child, event, isInLongPress, isCancel)
      mExpandableItemListeners.forEachReversed {
        it.onEventEnd(page, item, child, event, isInLongPress, isCancel)
      }
    }


    ///////////////////////////////
    //
    //          移动逻辑
    //
    ///////////////////////////////

    /**
     * 尝试发送一次刷新来回调 [mItemDecoration]
     *
     * 但值得注意的是，并不是直接调用 move()，而是在每一帧时回调 move() 方法，
     * 官方源码中经常这样操作，目的是为了统一回调时机，减少回调次数
     *
     * 这里调用后会在下一帧中回调 [mItemDecoration]，然后触发刷新操作
     */
    private fun tryPostMove() {
      if (!mIsMoving && mIsInLongPress == true) {
        mIsMoving = true
        mCoursePage?.course?.view?.invalidate()
      }
    }

    // Scroll 滚动不一定会导致 mItemDecoration 被回调，所以需要强制刷新
    private val mScrollYChangedListener = ICourseScrollControl.OnScrollYChangedListener { _, _ ->
      tryPostMove()
    }

    // 课表布局发生改变时也需要回调 mItemDecoration，然后触发 move()
    private val mOnLayoutChanged =
      View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        tryPostMove()
      }

    // 用于在每一帧时回调 move() 方法
    private val mItemDecoration = object : ItemDecoration {
      override fun onDrawBelow(canvas: Canvas, view: View) {
        if (mIsMoving && mIsInLongPress == true) {
          move() // 调用 move 方法
          mIsMoving = false
        }
      }

      /**
       * item 扩展的核心代码
       *
       * ### 不要直接调用该方法
       * 请使用 page.course.invalidate() 和设置 mIsMoving=true 来间接调用该方法 (invalidate() -> mItemDecoration -> move())。
       * 原因在于：
       * - 一帧只能回调一次 move，多次回调会多次计算导致效果出现问题 (比如滚轴会加倍移动)
       * - 如果手动调用，会导致调用 [changeScrollYIfNeed] 后出现鬼畜的移动效果
       */
      private fun move() {
        if (mIsInLongPress != true) return
        if (!mIsMoving) return
        val item = mTouchItem ?: return
        val view = mItemView ?: return
        val page = mCoursePage ?: return
        val course = page.course

        changeScrollYIfNeed()
        unfoldNoonDuskIfNeed()

        // 因为存在 scrollY 的改变和中午傍晚的展开导致的坐标系变化的问题
        // 所以要使用 ScrollView 的绝对位置来计算 Y 轴偏移量
        val absoluteY = course.scroll.getAbsoluteY(mPointerId)
        val scrollY = course.scroll.getScrollCourseY()

        val x = mLastMoveX
        val y = absoluteY + scrollY
        mExpandableHandler.onMove(page, item, view, x, y)
        mExpandableItemListeners.forEachReversed {
          it.onMove(page, item, view, x, y)
        }
      }
    }


    ///////////////////////////////
    //
    //           滚轴相关
    //
    ///////////////////////////////

    // 控制课表滚轴滚动
    private fun changeScrollYIfNeed() {
      val page = mCoursePage ?: return
      val course = page.course
      val scroll = course.scroll

      val absoluteY = scroll.getAbsoluteY(mPointerId)
      val scrollOuterHeight = scroll.getScrollOuterHeight()
      val moveBoundary = 100 // 移动的边界值
      val minV = 6 // 最小速度
      val maxV = 20 // 最大速度
      // 速度最小为 6，最大为 20，与边界的差值成线性关系

      // 向上滚动，即手指移到底部，需要显示下面的内容
      val isNeedScrollUp =
        absoluteY > scrollOuterHeight - moveBoundary
            && scroll.canCourseScrollVertically(1) // 没有滑到底

      // 向下滚动，即手指移到顶部，需要显示上面的内容
      val isNeedScrollDown =
        absoluteY < moveBoundary
            && scroll.canCourseScrollVertically(-1) // 没有滑到顶
      val velocity = if (isNeedScrollUp) {
        min((absoluteY - (scrollOuterHeight - moveBoundary)) / 2 + minV, maxV)
      } else if (isNeedScrollDown) {
        // 如果是向下滚动稍稍降低加速度，因为顶部手指可以移动出去，很容易满速
        -min(((moveBoundary - absoluteY) / 10 + minV), maxV)
      } else 0
      scroll.scrollCourseBy(velocity) // 这里调用后会回调 mScrollYChangedListener，然后又回调 move()
    }


    ///////////////////////////////
    //
    //         中午傍晚相关
    //
    ///////////////////////////////

    // 展开中午或者傍晚，如果需要的话
    private fun unfoldNoonDuskIfNeed() {
      val page = mCoursePage ?: return
      val view = mItemView ?: return
      if (!mIsLockedNoon) {
        val viewY = view.y.toInt()
        val isViewContainNoon =
          page.period.compareNoonPeriodByHeight(viewY) * page.period.compareNoonPeriodByHeight(viewY + view.height) <= 0
        if (isViewContainNoon) {
          // 这里需要展开中午
          page.fold.unfoldNoon()
          page.fold.lockFoldNoon() // 锁定中午，后面会还原
          mIsLockedNoon = true
          // 虽然不一定会展开成功，因为可能会在其他地方被锁住，但是解锁次数需要等于上锁次数才能完全解锁，所以最终还是仍被锁的
          // 其实只需要在移动期间禁止中午发生改变就可以了，不然会导致 item 大小跟随改变
        }
      }
      if (!mIsLockedDusk) {
        val viewY = view.y.toInt()
        val isViewContainDusk =
          page.period.compareDuskPeriodByHeight(viewY) * page.period.compareDuskPeriodByHeight(viewY + view.height) <= 0
        if (isViewContainDusk) {
          // 这里需要展开傍晚
          page.fold.unfoldDusk()
          page.fold.lockFoldDusk() // 锁定中午，后面会还原
          mIsLockedDusk = true
        }
      }
    }
  }
}