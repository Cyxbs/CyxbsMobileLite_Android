package com.cyxbs.pages.course.page.week

import android.annotation.SuppressLint
import android.widget.TextView
import com.cyxbs.components.config.string.Strings
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.R
import com.cyxbs.pages.course.page.controller.CourseController
import com.ndhzs.netlayout.view.NetLayout2
import java.util.Calendar

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/1 23:35
 */
open class CourseWeekController : CourseController(), ICourseWeek {

  override val nlWeek: NetLayout2 by R.id.course_nl_week.view()
  override val tvMonth: TextView by R.id.course_tv_month.view()
  override val tvMonWeek: TextView by R.id.course_tv_mon_week.view()
  override val tvMonMonth: TextView by R.id.course_tv_mon_month.view()
  override val tvTueWeek: TextView by R.id.course_tv_tue_week.view()
  override val tvTueMonth: TextView by R.id.course_tv_tue_month.view()
  override val tvWedWeek: TextView by R.id.course_tv_wed_week.view()
  override val tvWedMonth: TextView by R.id.course_tv_wed_month.view()
  override val tvThuWeek: TextView by R.id.course_tv_thu_week.view()
  override val tvThuMonth: TextView by R.id.course_tv_thu_month.view()
  override val tvFriWeek: TextView by R.id.course_tv_fri_week.view()
  override val tvFriMonth: TextView by R.id.course_tv_fri_month.view()
  override val tvSatWeek: TextView by R.id.course_tv_sat_week.view()
  override val tvSatMonth: TextView by R.id.course_tv_sat_month.view()
  override val tvSunWeek: TextView by R.id.course_tv_sun_week.view()
  override val tvSunMonth: TextView by R.id.course_tv_sun_month.view()

  override fun forEachWeekView(block: (week: TextView, month: TextView) -> Unit) {
    block.invoke(tvMonWeek, tvMonMonth)
    block.invoke(tvTueWeek, tvTueMonth)
    block.invoke(tvWedWeek, tvWedMonth)
    block.invoke(tvThuWeek, tvThuMonth)
    block.invoke(tvFriWeek, tvFriMonth)
    block.invoke(tvSatWeek, tvSatMonth)
    block.invoke(tvSunWeek, tvSunMonth)
  }

  override fun getWeekWeekView(weekNum: Int, block: (week: TextView, month: TextView) -> Unit) {
    checkWeekNum(weekNum)
    when (weekNum) {
      1 -> block.invoke(tvMonWeek, tvMonMonth)
      2 -> block.invoke(tvTueWeek, tvTueMonth)
      3 -> block.invoke(tvWedWeek, tvWedMonth)
      4 -> block.invoke(tvThuWeek, tvThuMonth)
      5 -> block.invoke(tvFriWeek, tvFriMonth)
      6 -> block.invoke(tvSatWeek, tvSatMonth)
      7 -> block.invoke(tvSunWeek, tvSunMonth)
    }
  }

  override fun onCreateView(page: ICoursePage) {
    super.onCreateView(page)
    page.course.addOnColumnWeightChangeListener { _, new, column ->
      nlWeek.setColumnShowWeight(column, new) // 同步列比重
    }
  }

  @SuppressLint("SetTextI18n")
  override fun setMonth(monDay: Calendar) {
    val calendar = monDay.clone() as Calendar
    tvMonth.text = Strings.getMonthStr(calendar.get(Calendar.MONTH) + 1)
    forEachWeekView { _, month ->
      month.text = Strings.getMonthNumStr(calendar.get(Calendar.DATE))
      calendar.add(Calendar.DATE, 1) // 天数加 1
    }
  }

  override fun getWeekNumStartWidth(weekNum: Int): Int {
    checkWeekNum(weekNum)
    return page.course.getColumnsWidth(0, weekNum - 1)
  }

  override fun getWeekNumEndWidth(weekNum: Int): Int {
    checkWeekNum(weekNum)
    return page.course.getColumnsWidth(0, weekNum)
  }

  private fun checkWeekNum(weekNum: Int) {
    require(weekNum in 1 .. 7) { "weekNum = $weekNum，超出边界！" }
  }
}