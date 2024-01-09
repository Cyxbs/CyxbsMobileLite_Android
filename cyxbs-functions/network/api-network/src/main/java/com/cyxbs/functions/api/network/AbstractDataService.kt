package com.cyxbs.functions.api.network

import com.cyxbs.components.router.ServiceManager
import com.cyxbs.functions.api.network.internal.IRequestService
import kotlinx.coroutines.flow.Flow

/**
 * 业务模块实现该模块，并提供对应的参数，之后使用 [request] 方法即可发起请求
 *
 * @author 985892345
 * @date 2023/10/25 20:00
 */
abstract class AbstractDataService(
  val parameters: LinkedHashMap<String, String>,
  val output: String, // 应该返回的数据格式，建议写成 json
) {

  private val mSourceService = ServiceManager.getImplOrThrow(IRequestService::class)

  /**
   * @param isForce 是否强制重新请求，如果不强制，则在未到更新间时隔会返回缓存值
   */
  suspend fun request(isForce: Boolean, vararg values: String): String {
    if (values.size != parameters.size) {
      throw IllegalArgumentException("参数个数不对应，应有 ${parameters.size}, 实有: ${values.size}")
    }
    return mSourceService.request(this@AbstractDataService, isForce, values.toList())
  }

  /**
   * 得到上一次更新的时间戳
   */
  suspend fun getLastResponseTimestamp(): Long? {
    return mSourceService.getLastResponseTimestamp(this)
  }

  /**
   * 观察更新
   */
  fun observeUpdate(): Flow<Boolean> {
    return mSourceService.observeUpdate(this)
  }
}