package com.cyxbs.components.view.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.TextView
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.extensions.android.size.dp2px
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 一个用于带有左侧行号的文本显示 View，并且支持双指放大缩小
 *
 * @author 985892345
 * @date 2022/9/23 19:19
 */
open class ScaleScrollTextView(
  context: Context,
  attrs: AttributeSet?
) : ViewGroup(context, attrs) {

  /**
   * 内部自动根据 \n 分行
   */
  var text: CharSequence?
    get() = mTvContent.text
    set(value) {
      mTvContent.text = value
      adjustSlideLine()
    }

  var hint: CharSequence?
    get() = mTvContent.hint
    set(value) {
      mTvContent.hint = value
    }

  var maxTextWidth: Int
    get() = mTvContent.maxWidth
    set(value) {
      mTvContent.maxWidth = value
    }

  /**
   * 设置字体大小，默认大小为 12F，单位 SP
   */
  fun setTextSize(sp: Float) {
    mTvLineNum.textSize = sp
    mTvContent.textSize = sp
  }

  /**
   * 设置侧边显示的行数
   */
  fun setSideLine(num: Int) {
    val count = if (num <= 0) 1 else num
    val builder = StringBuilder()
    repeat(count) {
      builder.append((it + 1).toString())
        .appendLine()
    }
    mTvLineNum.text = builder.toString()
  }

  /**
   * 自动侧边显示的行数
   */
  fun adjustSlideLine() {
    val textLine = mTvContent.text?.count { it == '\n' }?.plus(1) ?: 0
    val hintLine = mTvContent.hint?.count { it == '\n' }?.plus(1) ?: 0
    // 即使 hint 没有显示也不建议直接以 text 来计算行数，因为可能出现跳变
    setSideLine(maxOf(textLine, hintLine))
  }

  /**
   * 设置侧边显示行数的字体颜色
   */
  fun setSideTextColor(color: Int) {
    mTvLineNum.setTextColor(color)
  }

  /**
   * 设置侧边显示行数的背景颜色
   */
  fun setSideBackgroundColor(color: Int) {
    mTvLineNum.setBackgroundColor(color)
  }

  private val mScaleGestureDetector = ScaleGestureDetector(
    context,
    object : ScaleGestureDetector.OnScaleGestureListener {

      private var mLastFocusX = 0F
      private var mLastFocusY = 0F

      override fun onScale(detector: ScaleGestureDetector): Boolean {
        mTvLineNum.setTextSize(
          TypedValue.COMPLEX_UNIT_PX,
          mTvLineNum.textSize * detector.scaleFactor
        )
        mTvContent.setTextSize(
          TypedValue.COMPLEX_UNIT_PX,
          mTvContent.textSize * detector.scaleFactor
        )
        move(detector.focusX - mLastFocusX, detector.focusY - mLastFocusY)
        mLastFocusX = detector.focusX
        mLastFocusY = detector.focusY
        return true
      }

      override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        mLastFocusX = detector.focusX
        mLastFocusY = detector.focusY
        return true
      }

      override fun onScaleEnd(detector: ScaleGestureDetector) {
      }
    }
  )

  protected val mTvLineNum = TextView(context).apply {
    textSize = 12F
    setTextColor(if (isDaytimeMode()) 0xFF595959.toInt() else 0xFFDFDFDF.toInt())
    setBackgroundColor(if (isDaytimeMode()) 0xFFE6E6E6.toInt() else 0xFF434343.toInt())
    setPadding(12.dp2px, 4.dp2px, 10.dp2px, 4.dp2px)
    gravity = Gravity.END
    elevation = 1F // 显示在 mTvContent 之上
  }

  protected val mTvContent by lazyUnlock {
    createContentView(context).apply {
      textSize = 12F
      post {
        // 延后处理，因为内部会调用 setText()，如果在初始化时又设置 addTextChangedListener() 会陷入死循环
        setTextIsSelectable(true) // 内容可长按复制
      }
      setPadding(0, 4.dp2px, 10.dp2px, 0)
    }
  }

  protected open fun createContentView(context: Context): TextView {
    return TextView(context)
  }

  init {
    super.addView(
      mTvLineNum,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    )
    super.post {
      // 延后添加 mTvContent，构造器中直接添加会出问题
      super.addView(
        mTvContent,
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
      )
      adjustSlideLine()
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val wSize = MeasureSpec.getSize(widthMeasureSpec)
    val wMode = MeasureSpec.getMode(widthMeasureSpec)
    val hSize = MeasureSpec.getSize(heightMeasureSpec)
    val hMode = MeasureSpec.getMode(heightMeasureSpec)
    mTvLineNum.measure(
      MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.UNSPECIFIED)
    )
    mTvContent.measure(
      MeasureSpec.makeMeasureSpec(wSize - mTvLineNum.measuredWidth, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.UNSPECIFIED)
    )
    val wSpec = when (wMode) {
      MeasureSpec.EXACTLY -> widthMeasureSpec
      MeasureSpec.AT_MOST ->
        MeasureSpec.makeMeasureSpec(
          min(mTvLineNum.measuredWidth + mTvContent.measuredWidth, wSize),
          MeasureSpec.EXACTLY
        )

      MeasureSpec.UNSPECIFIED ->
        MeasureSpec.makeMeasureSpec(
          max(mTvLineNum.measuredWidth + mTvContent.measuredWidth, wSize),
          MeasureSpec.EXACTLY
        )

      else -> error("")
    }
    val hSpec = when (hMode) {
      MeasureSpec.EXACTLY -> heightMeasureSpec
      MeasureSpec.AT_MOST ->
        MeasureSpec.makeMeasureSpec(
          min(max(mTvLineNum.measuredHeight, mTvContent.measuredHeight), hSize),
          MeasureSpec.EXACTLY
        )

      MeasureSpec.UNSPECIFIED ->
        MeasureSpec.makeMeasureSpec(
          maxOf(mTvLineNum.measuredHeight, mTvContent.measuredHeight, hSize),
          MeasureSpec.EXACTLY
        )

      else -> error("")
    }
    super.onMeasure(wSpec, hSpec)
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    mTvLineNum.layout(0, 0, mTvLineNum.measuredWidth, mTvLineNum.measuredHeight)
    val left = mTvLineNum.measuredWidth + 6.dp2px
    mTvContent.layout(left, 0, left + mTvContent.measuredWidth, mTvContent.measuredHeight)
  }

  private var mInitialX = 0F
  private var mInitialY = 0F
  private var mLastMoveX = 0F
  private var mLastMoveY = 0F

  private var mIsFirstMove = false
  private var mIsDisallowIntercept = false

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    val x = ev.x
    val y = ev.y
    when (ev.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        mLockCount = 0
        mInitialX = x
        mInitialY = y
        mLastMoveX = x
        mLastMoveY = y
        mIsClick = true
        mIsFirstMove = true
        mIsDisallowIntercept = false
        if (canScrollHorizontally(1) || canScrollHorizontally(-1)
          || canScrollVertically(1) || canScrollHorizontally(-1)
        ) {
          mIsDisallowIntercept = true
          requestParentDisallowIntercept(true)
        }
      }

      MotionEvent.ACTION_MOVE -> {
        if (mIsFirstMove) {
          mIsFirstMove = false
          if (mIsDisallowIntercept) {
            if (x > mLastMoveX) {
              // 手指右移
              if (!canScrollHorizontally(-1)) {
                requestParentDisallowIntercept(false)
              }
            } else if (x < mLastMoveX) {
              // 手指左移
              if (!canScrollHorizontally(1)) {
                requestParentDisallowIntercept(false)
              }
            } else if (y > mLastMoveY) {
              // 手指下移
              if (!canScrollVertically(-1)) {
                requestParentDisallowIntercept(false)
              }
            } else if (y < mLastMoveY) {
              // 手指上移
              if (!canScrollVertically(1)) {
                requestParentDisallowIntercept(false)
              }
            }
          }
        }
      }

      MotionEvent.ACTION_POINTER_DOWN -> {
        if (mIsClick) {
          mIsClick = false
          requestParentDisallowIntercept(true)
        }
      }
    }
    // 因为 onInterceptTouchEvent 和 onTouchEvent 事件的不完整性，所以只能在这里处理
    mScaleGestureDetector.onTouchEvent(ev)
    return super.dispatchTouchEvent(ev).also {
      when (ev.actionMasked) {
        MotionEvent.ACTION_MOVE -> {
          mLastMoveX = x
          mLastMoveY = y
        }
      }
    }
  }

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    val x = ev.x
    val y = ev.y
    when (ev.actionMasked) {
      MotionEvent.ACTION_POINTER_DOWN -> {
        mIsClick = false
        return true
      }

      MotionEvent.ACTION_MOVE -> {
        if (abs(mLastMoveX - x) > mTouchSlop || abs(mLastMoveY - y) > mTouchSlop) {
          if (x > mLastMoveX && canScrollHorizontally(-1)
            || x < mLastMoveX && canScrollHorizontally(1)
            || y > mLastMoveY && canScrollVertically(-1)
            || y < mLastMoveY && canScrollVertically(1)
          ) {
            requestParentDisallowIntercept(true)
            mIsClick = false
            return true
          }
        }
      }
    }
    return false
  }

  private var mLockCount = 0

  protected fun requestParentDisallowIntercept(disallowIntercept: Boolean) {
    if (disallowIntercept) {
      mLockCount++
      if (mLockCount == 1) {
        parent.requestDisallowInterceptTouchEvent(true)
      }
    } else {
      if (mLockCount > 0) {
        mLockCount--
        if (mLockCount == 0) {
          parent.requestDisallowInterceptTouchEvent(false)
        }
      }
    }
  }


  protected var mIsClick = true

  private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private val mLongPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()

  private val mClickRunnable = Runnable { performClick() }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    val y = event.y
    when (event.actionMasked) {
      MotionEvent.ACTION_MOVE -> {
        // 多根手指时交给 mScaleGestureDetector 处理
        if (event.pointerCount == 1) {
          move(x - mLastMoveX, y - mLastMoveY)
        }
        if (mIsClick && (abs(mLastMoveX - x) > mTouchSlop || abs(mLastMoveY - y) > mTouchSlop)) {
          mIsClick = false
        }
      }

      MotionEvent.ACTION_UP -> {
        if (mIsClick && event.eventTime - event.downTime < mLongPressTimeout) {
          if (!post(mClickRunnable)) { // 遵循官方做法，在 post 中调用防止堵塞
            performClick()
          }
        }
      }
    }
    return true
  }

  private fun move(dx: Float, dy: Float) {
    mTvContent.translationX += dx
    if (dx > 0) {
      // 手指向右移动
      if (!canScrollHorizontally(-1)) {
        mTvContent.translationX = 0F
      }
    } else {
      // 手指向左移动
      if (mTvContent.width > width - mTvLineNum.width) {
        if (!canScrollHorizontally(1)) {
          mTvContent.translationX = (width - mTvLineNum.width - mTvContent.width).toFloat()
        }
      } else {
        mTvContent.translationX = 0F
      }
    }
    mTvContent.translationY += dy
    mTvLineNum.translationY += dy
    if (dy > 0) {
      // 手指向下移动
      if (!canScrollVertically(-1)) {
        mTvLineNum.translationY = 0F
        mTvContent.translationY = 0F
      }
    } else {
      // 手指向上移动
      if (mTvLineNum.height > height) {
        if (!canScrollVertically(1)) {
          mTvLineNum.translationY = (height - mTvLineNum.height).toFloat()
          mTvContent.translationY = (height - mTvLineNum.height).toFloat()
        }
      } else {
        mTvLineNum.translationY = 0F
        mTvContent.translationY = 0F
      }
    }
  }

  /**
   * > 0: 手指能否向上滑动
   * < 0: 手指能都向下滑动
   */
  override fun canScrollVertically(direction: Int): Boolean {
    return if (direction > 0) {
      mTvLineNum.translationY + mTvLineNum.height < height
    } else if (direction < 0) {
      mTvLineNum.translationY < 0F
    } else false
  }

  /**
   * > 0: 手指能否向左滑动
   * < 0: 手指能都向右滑动
   */
  override fun canScrollHorizontally(direction: Int): Boolean {
    return if (direction > 0) {
      mTvContent.translationX + mTvContent.width > width - mTvLineNum.width
    } else if (direction < 0) {
      mTvContent.translationX < 0F
    } else false
  }

  private fun isDaytimeMode(): Boolean {
    val uiMode = context.resources.configuration.uiMode
    return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
  }
}