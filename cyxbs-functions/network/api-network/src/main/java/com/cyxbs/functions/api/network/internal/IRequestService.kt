package com.cyxbs.functions.api.network.internal

import com.cyxbs.functions.api.network.AbstractDataService
import kotlinx.coroutines.flow.Flow

/**
 * 用于实现数据源的请求接口，由 source 模块实现，其他模块不需要实现
 *
 * @author 985892345
 * @date 2023/10/25 20:27
 */
interface IRequestService {

  suspend fun request(
    dataSource: AbstractDataService,
    isForce: Boolean,
    values: List<String>,
  ): String

  suspend fun getLastResponseTimestamp(dataSource: AbstractDataService): Long?

  fun observeUpdate(dataSource: AbstractDataService): Flow<Boolean>
}