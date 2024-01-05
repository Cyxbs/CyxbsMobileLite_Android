package com.cyxbs.pages.course.page.timeline

import android.widget.TextView
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.R
import com.cyxbs.pages.course.page.controller.CourseController

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/2 21:15
 */
open class CourseTimelineController : CourseController(), ICourseTimeline {

  final override fun compareTimelineColumn(column: Int): Int {
    return if (column < TIMELINE_LEFT) -1 else if (column > TIMELINE_RIGHT) 1 else 0
  }

  final override fun forEachTimelineRow(block: (row: Int) -> Unit) {
    for (row in TIMELINE_TOP .. TIMELINE_BOTTOM) {
      block.invoke(row)
    }
  }

  final override fun forEachTimelineColumn(block: (column: Int) -> Unit) {
    for (column in TIMELINE_LEFT .. TIMELINE_RIGHT) {
      block.invoke(column)
    }
  }

  final override fun getTimelineStartWidth(): Int {
    return page.course.getColumnsWidth(0, TIMELINE_LEFT - 1) + page.course.view.paddingLeft
  }

  final override fun getTimelineEndWidth(): Int {
    return page.course.getColumnsWidth(0, TIMELINE_RIGHT) + page.course.view.paddingLeft
  }

  override val tvLesson1        by R.id.course_tv_lesson_1.view<TextView>()
  override val tvLesson2        by R.id.course_tv_lesson_2.view<TextView>()
  override val tvLesson3        by R.id.course_tv_lesson_3.view<TextView>()
  override val tvLesson4        by R.id.course_tv_lesson_4.view<TextView>()
  override val tvLesson5        by R.id.course_tv_lesson_5.view<TextView>()
  override val tvLesson6        by R.id.course_tv_lesson_6.view<TextView>()
  override val tvLesson7        by R.id.course_tv_lesson_7.view<TextView>()
  override val tvLesson8        by R.id.course_tv_lesson_8.view<TextView>()
  override val tvLesson9        by R.id.course_tv_lesson_9.view<TextView>()
  override val tvLesson10       by R.id.course_tv_lesson_10.view<TextView>()
  override val tvLesson11       by R.id.course_tv_lesson_11.view<TextView>()
  override val tvLesson12       by R.id.course_tv_lesson_12.view<TextView>()
  override val tvNoon           by R.id.course_tv_noon.view<TextView>()
  override val tvDusk           by R.id.course_tv_dusk.view<TextView>()

  companion object {
    private const val TIMELINE_LEFT = 0 // 时间轴开始列
    private const val TIMELINE_RIGHT = 0 // 时间轴结束列

    private const val TIMELINE_TOP = 0 // 时间轴开始行
    private const val TIMELINE_BOTTOM = 13 // 时间轴开始行
  }



  /////////////////////////////////////
  //
  //             业务逻辑区
  //
  /////////////////////////////////////

  override fun onCreateView(page: ICoursePage) {
    super.onCreateView(page)
    initTimeline()
  }

  protected open fun initTimeline() {
    // 下面这个 for 用于设置时间轴的初始化宽度
    forEachTimelineColumn {
      page.course.setColumnShowWeight(it, 0.8F)
    }
  }
}