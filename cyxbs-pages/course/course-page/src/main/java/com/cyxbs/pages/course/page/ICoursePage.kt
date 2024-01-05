package com.cyxbs.pages.course.page

import android.view.View
import com.cyxbs.pages.course.page.fold.expose.ICourseFold
import com.cyxbs.pages.course.page.period.ICoursePeriod
import com.cyxbs.pages.course.page.timeline.ICourseTimeline
import com.cyxbs.pages.course.page.week.ICourseWeek
import com.cyxbs.pages.course.page.overlap.IOverlapContainer
import com.cyxbs.pages.course.page.today.ICourseToday
import com.cyxbs.pages.course.page.touch.ICourseTouch
import com.cyxbs.pages.course.view.course.ICourseViewGroup
import com.cyxbs.pages.course.view.scroll.ICourseScroll

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/2 20:53
 */
interface ICoursePage {

  val root: View

  /**
   * 课表布局，保持接口通信，降低耦合度
   */
  val course: ICourseViewGroup

  /**
   * 课表滚轴
   */
  val scroll: ICourseScroll

  /**
   * 课表周相关
   */
  val week: ICourseWeek

  /**
   * 课表折叠相关
   */
  val timeline: ICourseTimeline

  /**
   * 课表左侧时间轴
   */
  val period: ICoursePeriod

  /**
   * 课表上午、中午、下午、傍晚、晚上时间段
   */
  val fold: ICourseFold

  /**
   * 课表 item 的重叠处理
   */
  val overlap: IOverlapContainer

  /**
   * 显示今天相关，比如课表显示正处于今天的淡蓝色背景
   */
  val today: ICourseToday

  /**
   * 事件分发相关
   */
  val touch: ICourseTouch
}