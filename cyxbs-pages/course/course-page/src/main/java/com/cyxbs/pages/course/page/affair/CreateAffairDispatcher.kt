package com.cyxbs.pages.course.page.affair

import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.affair.expose.ICreateAffairConfig
import com.cyxbs.pages.course.page.affair.expose.ITouchAffairItem
import com.cyxbs.pages.course.page.affair.expose.ITouchCallback
import com.cyxbs.pages.course.page.dispatcher.AbstractLongPressDispatcher
import com.cyxbs.pages.course.page.dispatcher.ILongPressTouchHandler
import com.cyxbs.pages.course.page.fold.FoldState
import com.cyxbs.pages.course.page.touch.ScrollTouchHandler
import com.cyxbs.pages.course.view.course.ICourseViewGroup
import com.cyxbs.pages.course.view.utils.forEachReversed
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.*
import kotlin.math.abs

/**
 * 长按生成事务的事件分发者
 *
 * ## 注意
 * - 如果你需要添加一些约束性的交互 (比如某时候不允许生成 View)，建议写在 [iCreateAffair] 中
 *
 * @author 985892345
 * @date 2022/9/19 14:48
 */
class CreateAffairDispatcher(
  val page: ICoursePage,
  val iCreateAffair: ICreateAffairConfig = ICreateAffairConfig.Default, // 如果你需要与外界进行交互，建议写在这个里面
) : AbstractLongPressDispatcher() {
  
  /**
   * 设置点击 [ITouchAffairItem] 的点击监听
   */
  fun setOnClickListener(onClick: ITouchAffairItem.() -> Unit) {
    mOnClickListener = onClick
  }
  
  /**
   * 长按手指移动的回调
   */
  fun addTouchCallback(callback: ITouchCallback) {
    mTouchCallbackImpl.mTouchCallbacks.add(callback)
  }
  
  fun removeTouchCallback(callback: ITouchCallback) {
    mTouchCallbackImpl.mTouchCallbacks.remove(callback)
  }
  
  // 暂时保存 ITouchAffairItem 用于取消显示
  private val mTouchAffairViews = arrayListOf<ITouchAffairItem>()
  private var mIsAllowIntercept = true
  private var mOnClickListener: (ITouchAffairItem.() -> Unit)? = null
  
  override fun handleEvent(event: IPointerEvent, view: ViewGroup): ILongPressTouchHandler? {
    if (mIsAllowIntercept) {
      if (iCreateAffair.isValidDown(page, event)) {
        return getHandler(event)
      }
    }
    return null
  }
  
  override fun onCancelLongPress(
    event: IPointerEvent,
    view: ViewGroup,
    initialX: Int,
    initialY: Int
  ): IPointerTouchHandler? {
    return when (event.action) {
      UP -> {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
        if (abs(x - initialX) <= touchSlop && abs(y - initialY) <= touchSlop) {
          // 这里说明移动的距离小于 touchSlop，但还是得把点击的事务给绘制上，但是只有一格
          val item = initializeTouchAffairItem(event)
          val course = page.course
          val initialRow = course.getRow(y)
          val initialColumn = course.getColumn(x)
          item.onMoveStart(course, initialRow, initialColumn)
          item.onMoveEnd(course)
          mTouchCallbackImpl.onShowTouchAffairItem(course, item)
        }
        null
      }
      MOVE -> ScrollTouchHandler // MOVE 中被取消长按说明手指移动距离过大
      else -> null
    }
  }
  
  override fun onDispatchTouchEvent(event: MotionEvent, view: ViewGroup) {
    super.onDispatchTouchEvent(event, view)
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        mIsAllowIntercept = true // 还原
        val item = page.course.container.findItemUnderByXY(event.x.toInt(), event.y.toInt())
        if (item !is ITouchAffairItem) {
          // DOWN 事件时如果点击的不是 TouchAffair，则取消已经显示的 TouchAffair
          mTouchAffairViews.forEach {
            if (it.isInShow()) {
              mIsAllowIntercept = false // 如果存在正在显示的 TouchAffair，则这次 DOWN 事件不拦截
              it.cancelShow() // 取消正在显示的 TouchAffair
            }
          }
          mTouchAffairViews.clear()
        }
      }
      MotionEvent.ACTION_POINTER_DOWN -> {
        mIsAllowIntercept = true // 但第二跟手指触摸时就允许拦截 (如果没有被其他拦截的话)
      }
    }
  }
  
  /**
   * 得到一个 [CreateAffairHandler]
   */
  private fun getHandler(event: IPointerEvent): CreateAffairHandler {
    val touchAffairItem = initializeTouchAffairItem(event)
    return CreateAffairHandler(page.course, mTouchCallbackImpl, iCreateAffair, touchAffairItem)
  }
  
  /**
   * 初始化 [ITouchAffairItem]
   */
  private fun initializeTouchAffairItem(event: IPointerEvent): ITouchAffairItem {
    val item = iCreateAffair.createTouchAffairItem(page.course, event)
    mTouchAffairViews.add(item)
    // 这里统一给 TouchAffairItem 设置点击事件
    item.setOnClickListener { mOnClickListener?.invoke(item) }
    return item
  }
  
  /**
   * [ITouchCallback] 的实现类
   */
  private val mTouchCallbackImpl = object : ITouchCallback {
    
    val mTouchCallbacks = arrayListOf<ITouchCallback>()
    
    override fun onLongPressed(pointerId: Int, row: Int, column: Int) {
      mTouchCallbacks.forEachReversed { it.onLongPressed(pointerId, row, column) }
    }
    
    override fun onTouchMove(pointerId: Int, row: Int, touchRow: Int, showRow: Int) {
      unfoldNoonOrDuskIfNecessary(row, touchRow)
      mTouchCallbacks.forEachReversed {
        it.onTouchMove(pointerId, row, touchRow, showRow)
      }
    }
    
    override fun onTouchEnd(
      pointerId: Int,
      row: Int,
      touchRow: Int,
      showRow: Int,
      isCancel: Boolean
    ) {
      mTouchCallbacks.forEachReversed {
        it.onTouchEnd(pointerId, row, touchRow, showRow, isCancel)
      }
    }
    
    override fun onShowTouchAffairItem(course: ICourseViewGroup, item: ITouchAffairItem) {
      mTouchCallbacks.forEachReversed {
        it.onShowTouchAffairItem(course, item)
      }
    }
    
    /**
     * 判断当前滑动中是否需要自动展开中午或者傍晚时间段
     * @param initialRow 最开始触摸的行数
     * @param touchRow 当前触摸的行数
     */
    private fun unfoldNoonOrDuskIfNecessary(initialRow: Int, touchRow: Int) {
      val fold = page.fold
      val period = page.period
      when (fold.getNoonRowState()) {
        FoldState.FOLD, FoldState.FOLD_ANIM, FoldState.UNKNOWN -> {
          if (period.compareNoonPeriodByRow(initialRow) * period.compareNoonPeriodByRow(touchRow) <= 0) {
            fold.unfoldNoon()
          }
        }
        else -> {}
      }
      when (fold.getDuskRowState()) {
        FoldState.FOLD, FoldState.FOLD_ANIM, FoldState.UNKNOWN -> {
          if (period.compareDuskPeriodByRow(initialRow) * period.compareDuskPeriodByRow(touchRow) <= 0) {
            fold.unfoldDusk()
          }
        }
        else -> {}
      }
    }
  }
}