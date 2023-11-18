package com.cyxbs.pages.source.single

import com.cyxbs.functions.api.network.AbstractDataService
import com.g985892345.provider.annotation.ImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/27 21:23
 */
@ImplProvider(AbstractDataService::class, "课表")
object TestCourseDataServiceImpl : AbstractDataService(
  linkedMapOf(
    "stu_num" to "学号"
  ),
  """
    // 用于测试，无返回数据格式显示
  """.trimIndent()
)