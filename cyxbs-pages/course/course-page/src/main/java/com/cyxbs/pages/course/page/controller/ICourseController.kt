package com.cyxbs.pages.course.page.controller

import com.cyxbs.pages.course.page.ICoursePage

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/3 14:30
 */
interface ICourseController {

  fun onCreateView(page: ICoursePage)

  fun onBind()

  fun onUnBind()

  fun onDestroyView()
}