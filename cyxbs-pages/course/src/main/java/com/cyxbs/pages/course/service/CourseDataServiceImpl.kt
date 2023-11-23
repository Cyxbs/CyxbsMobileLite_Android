package com.cyxbs.pages.course.service

import com.cyxbs.functions.api.network.AbstractDataService
import com.g985892345.provider.annotation.ImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/27 21:23
 */
@ImplProvider(AbstractDataService::class, "课表")
object CourseDataServiceImpl : AbstractDataService(
  linkedMapOf(
    "stu_num" to "学号"
  ),
  """
    // 待补充
  """.trimIndent()
)