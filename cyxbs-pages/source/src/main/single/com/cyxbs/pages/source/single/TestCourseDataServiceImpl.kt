package com.cyxbs.pages.source.single

import com.cyxbs.functions.api.network.AbstractDataService
import com.g985892345.provider.annotation.SingleImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/27 21:23
 */
@SingleImplProvider(AbstractDataService::class, "课表")
object TestCourseDataServiceImpl : AbstractDataService(
  listOf(
    "stu_num" to "学号"
  )
)