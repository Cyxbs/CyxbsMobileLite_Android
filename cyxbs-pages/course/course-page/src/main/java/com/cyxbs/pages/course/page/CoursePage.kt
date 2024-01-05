package com.cyxbs.pages.course.page

import android.view.View
import com.cyxbs.pages.course.page.fold.CourseFoldController
import com.cyxbs.pages.course.page.overlap.OverlapContainerController
import com.cyxbs.pages.course.page.period.CoursePeriodController
import com.cyxbs.pages.course.page.timeline.CourseTimelineController
import com.cyxbs.pages.course.page.today.CourseTodayController
import com.cyxbs.pages.course.page.touch.CourseTouchController
import com.cyxbs.pages.course.page.week.CourseWeekController
import com.cyxbs.pages.course.view.course.ICourseViewGroup
import com.cyxbs.pages.course.view.course.base.AbstractCourseViewGroup
import com.cyxbs.pages.course.view.scroll.ICourseScroll
import com.cyxbs.pages.course.view.scroll.base.AbstractCourseScrollView
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.utils.view.bind.BindView

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/2 21:22
 */
open class CoursePage : ICoursePage {

  private var _root: View? = null

  override val root: View
    get() = _root!!

  override val course: ICourseViewGroup by R.id.course_courseLayout.view<AbstractCourseViewGroup>()

  override val scroll: ICourseScroll by R.id.course_scrollView.view<AbstractCourseScrollView>()

  override val week by lazyUnlock { CourseWeekController() }

  override val timeline by lazyUnlock { CourseTimelineController() }

  override val period by lazyUnlock { CoursePeriodController() }

  override val fold by lazyUnlock { CourseFoldController() }

  override val overlap by lazyUnlock { OverlapContainerController() }

  override val today by lazyUnlock { CourseTodayController() }

  override val touch by lazyUnlock { CourseTouchController() }

  private val controllers by lazy {
    listOf(week, timeline, period, fold, overlap, today, touch)
  }

  fun create(root: View) {
    controllers.forEach { it.onCreateView(this) }
  }

  fun bind() {
    controllers.forEach { it.onBind() }
  }

  fun unBind() {
    controllers.forEach { it.onUnBind() }
  }

  fun destroy() {
    controllers.forEach { it.onDestroyView() }
  }

  protected fun <T: View> Int.view(): BindView<T> = BindView(this, ::root)
}