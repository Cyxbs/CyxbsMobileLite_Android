package com.cyxbs.pages.course.view.scroll.base

import android.content.Context
import android.util.AttributeSet
import com.cyxbs.pages.course.view.scroll.ICourseScroll
import com.g985892345.android.utils.view.scroll.NestedScrollView2

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:37
 */
abstract class AbstractCourseScrollView(
  context: Context,
  attrs: AttributeSet? = null,
) : NestedScrollView2(context, attrs), ICourseScroll {
}