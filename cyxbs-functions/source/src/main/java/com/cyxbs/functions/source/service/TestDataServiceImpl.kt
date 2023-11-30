package com.cyxbs.functions.source.service

import com.cyxbs.functions.api.network.AbstractDataService
import com.g985892345.provider.annotation.ImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/26 23:20
 */
@ImplProvider(AbstractDataService::class, "测试")
object TestDataServiceImpl : AbstractDataService(
  linkedMapOf(),
  """
    // 用于测试，无返回数据格式显示
  """.trimIndent()
) {
}