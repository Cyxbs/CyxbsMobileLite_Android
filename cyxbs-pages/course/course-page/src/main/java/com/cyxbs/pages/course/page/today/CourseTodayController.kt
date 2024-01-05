package com.cyxbs.pages.course.page.today

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.view.doOnAttach
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.R
import com.cyxbs.pages.course.page.controller.CourseController
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.extensions.android.size.dp2pxF
import com.ndhzs.netlayout.draw.ItemDecoration

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/4 15:24
 */
open class CourseTodayController : CourseController(), ICourseToday {
  
  protected open val mTodayHighlightHelper by lazyUnlock {
    TodayHighlightHelper(page).also {
      page.week.nlWeek.addItemDecoration(it)
      page.course.addItemDecoration(it)
    }
  }
  
  override fun showToday(weekNum: Int) {
    mTodayHighlightHelper.setWeekNum(weekNum)
    page.week.getWeekWeekView(weekNum) { week, month ->
      val color = com.cyxbs.components.config.R.color.config_white_black.color
      week.setTextColor(color)
      month.setTextColor(color)
    }
  }
  
  protected open class TodayHighlightHelper(
    val page: ICoursePage
  ) : ItemDecoration {
    
    private var mLeft = 0F
    private var mRight = 0F
    
    private val mRadius = 8.dp2pxF
    
    private val mWeekPaint = Paint().apply {
      color = com.cyxbs.components.config.R.color.config_level_four_font_color.color
      style = Paint.Style.FILL
      isAntiAlias = true
    }
    
    private val mCoursePaint = Paint().apply {
      color = R.color.course_page_today_highlight.color
      style = Paint.Style.FILL
    }
    
    override fun onDrawBelow(canvas: Canvas, view: View) {
      when (view) {
        page.course -> {
          canvas.drawRect(mLeft, 0F, mRight, view.height.toFloat(), mCoursePaint)
        }
        page.week.nlWeek -> {
          canvas.drawRect(mLeft, mRadius, mRight, view.height.toFloat(), mCoursePaint)
          canvas.drawRoundRect(mLeft, 0F, mRight, view.height.toFloat(), mRadius, mRadius, mWeekPaint)
        }
      }
    }
    
    fun setWeekNum(weekNum: Int) {
      mLeft = page.week.getWeekNumStartWidth(weekNum).toFloat()
      mRight = page.week.getWeekNumEndWidth(weekNum).toFloat()
      if (mLeft == 0F && mRight == 0F) {
        // 如果都为 0，说明此时没有开始布局
        page.course.view.doOnAttach {
          mLeft = page.week.getWeekNumStartWidth(weekNum).toFloat()
          mRight = page.week.getWeekNumEndWidth(weekNum).toFloat()
          page.week.nlWeek.invalidate()
          page.course.view.invalidate()
        }
      } else {
        page.week.nlWeek.invalidate()
        page.course.view.invalidate()
      }
    }
  }
}