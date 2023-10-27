package com.cyxbs.pages.source.single

import com.cyxbs.functions.api.network.AbstractDataService
import com.g985892345.provider.annotation.SingleImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/26 23:20
 */
@SingleImplProvider(AbstractDataService::class, "测试")
object TestDataServiceImpl : AbstractDataService(
  listOf()
) {
}