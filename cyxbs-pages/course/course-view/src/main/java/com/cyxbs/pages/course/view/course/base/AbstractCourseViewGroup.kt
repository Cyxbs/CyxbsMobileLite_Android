package com.cyxbs.pages.course.view.course.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.cyxbs.pages.course.view.R
import com.cyxbs.pages.course.view.course.ICourseViewGroup
import com.cyxbs.pages.course.view.course.lp.ItemLayoutParams
import com.cyxbs.pages.course.view.course.lp.XmlLayoutParams
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.view.NetLayout2

/**
 * 用于一些简单的方法重写
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:24
 */
abstract class AbstractCourseViewGroup(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseLayoutStyle,
  defStyleRes: Int = 0
) : NetLayout2(context, attrs, defStyleAttr, defStyleRes), ICourseViewGroup {
  
  override var debug: Boolean
    get() = DEBUG
    set(value) { DEBUG = value }

  override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
    return XmlLayoutParams(context, attrs)
  }

  override fun generateLayoutParams(lp: LayoutParams): LayoutParams {
    error("不允许使用除 ${ItemLayoutParams::class.simpleName} 以外的 LayoutParams")
  }

  override fun generateDefaultLayoutParams(): LayoutParams {
    error("不允许直接 addView(View)")
  }

  override fun checkLayoutParams(p: LayoutParams?): Boolean {
    return p is ItemLayoutParams
  }

  private var mCompareLayoutParams: Comparator<ItemLayoutParams>? = null

  final override fun setCompareLayoutParams(compare: Comparator<ItemLayoutParams>) {
    mCompareLayoutParams = compare
  }

  final override fun compareLayoutParams(o1: NetLayoutParams, o2: NetLayoutParams): Int {
    return mCompareLayoutParams?.compare(o1 as ItemLayoutParams, o2 as ItemLayoutParams)
      ?: super.compareLayoutParams(o1, o2)
  }

  final override fun measureChildWithRatio(
    child: View,
    parentWidthMeasureSpec: Int,
    parentHeightMeasureSpec: Int,
    childWidthRatio: Float,
    childHeightRatio: Float
  ) {
    val lp = child.layoutParams.net()
    val parentWidth = MeasureSpec.getSize(parentWidthMeasureSpec) - paddingLeft - paddingRight
    val wMode = MeasureSpec.getMode(parentWidthMeasureSpec)
    val childWidth = (childWidthRatio * parentWidth).toInt()
    val childWidthMeasureSpec = getChildMeasureSpec(
      MeasureSpec.makeMeasureSpec(childWidth, wMode),
      lp.leftMargin + lp.rightMargin, lp.width
    )

    val parentHeight = MeasureSpec.getSize(parentHeightMeasureSpec) - paddingTop - paddingBottom
    val childHeight = (childHeightRatio * parentHeight).toInt()
    val childHeightMeasureSpec = getChildMeasureSpec(
      MeasureSpec.makeMeasureSpec(
        childHeight,
        /*
        * 这里为什么直接给 EXACTLY ?
        * 1、目前需求（22年）课表在开始时不显示中午和傍晚时间段，我设计的 NetLayout 可以把高度设置成
        *    wrap_content，再调用 setRowInitialWeight() 使中午和傍晚时间段初始的比重为 0，
        *    从而实现不展开中午和傍晚时刚好铺满外控件大小，即外面的 ScrollView 刚好不能滚动
        * 2、课表如果要显示中午和傍晚时间段，则外布局需要包裹一个 NestedScrollView，这时，父布局得到的
        *    测量模式为 UNSPECIFIED，该模式会使课表初始状态不再填充父布局，所以需要把子 View 的测量改为 EXACTLY 模式
        * 3、改子 View 的测量模式原因在于 TextView 等一般的 View 在收到 UNSPECIFIED 模式时会使用自身合适的高度值，
        *    而不是 childHeight 这个值，就会导致课表初始状态不再填充父布局
        * */
        MeasureSpec.EXACTLY
      ),
      lp.topMargin + lp.bottomMargin, lp.height
    )

    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
  }
}