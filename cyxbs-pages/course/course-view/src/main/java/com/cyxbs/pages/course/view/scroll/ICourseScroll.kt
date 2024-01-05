package com.cyxbs.pages.course.view.scroll

import android.view.ViewGroup
import com.cyxbs.pages.course.view.course.ICourseScrollControl

/**
 * 课表滚轴接口
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 14:06
 */
interface ICourseScroll : ICourseScrollControl {

  val view: ViewGroup
    get() = this as ViewGroup
}