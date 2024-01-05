package com.cyxbs.pages.course.view.course.controller

import android.view.ViewGroup
import com.cyxbs.pages.course.view.course.CourseViewGroup
import com.cyxbs.pages.course.view.course.ICourseScrollControl

/**
 * 负责处理课表滚轴的父类
 *
 * @author 985892345
 * @date 2023/11/30 23:05
 */
class CourseScrollController(
  val course: CourseViewGroup
) : ICourseScrollControl {

  override fun scrollCourseBy(dy: Int) {
    getScrollImpl().scrollCourseBy(dy)
  }

  override fun scrollCourseY(y: Int) {
    getScrollImpl().scrollCourseY(y)
  }

  override fun getScrollCourseY(): Int {
    return getScrollImpl().getScrollCourseY()
  }

  override fun getAbsoluteY(pointerId: Int): Int {
    return getScrollImpl().getAbsoluteY(pointerId)
  }

  override fun getScrollOuterHeight(): Int {
    return getScrollImpl().getScrollOuterHeight()
  }

  override fun getScrollInnerHeight(): Int {
    return getScrollImpl().getScrollInnerHeight()
  }

  override fun canCourseScrollVertically(direction: Int): Boolean {
    return getScrollImpl().canCourseScrollVertically(direction)
  }

  override fun addOnScrollYChanged(l: ICourseScrollControl.OnScrollYChangedListener) {
    getScrollImpl().addOnScrollYChanged(l)
  }

  override fun removeOnScrollYChanged(l: ICourseScrollControl.OnScrollYChangedListener) {
    getScrollImpl().removeOnScrollYChanged(l)
  }

  private fun getScrollImpl(): ICourseScrollControl {
    var parent = course.parent
    while (parent is ViewGroup) {
      if (parent is ICourseScrollControl) break
      parent = parent.parent
    }
    if (parent !is ICourseScrollControl) error("在父 View 中找不到 ${ICourseScrollControl::class.simpleName} 的实现类")
    return parent
  }
}