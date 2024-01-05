package com.cyxbs.pages.course.page.period

import com.cyxbs.pages.course.page.period.am.IAmPeriod
import com.cyxbs.pages.course.page.period.dusk.IDuskPeriod
import com.cyxbs.pages.course.page.period.night.INightPeriod
import com.cyxbs.pages.course.page.period.noon.INoonPeriod
import com.cyxbs.pages.course.page.period.pm.IPmPeriod

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 14:52
 */
interface ICoursePeriod : IAmPeriod, INoonPeriod, IPmPeriod, IDuskPeriod, INightPeriod {
  
  /**
   * 得到某节课开始前的高度值（距离课表上边缘）
   */
  fun getLessonStartHeight(num: Int): Int
  
  /**
   * 得到某节课结束时的高度值（距离课表上边缘）
   */
  fun getLessonEndHeight(num: Int): Int
}