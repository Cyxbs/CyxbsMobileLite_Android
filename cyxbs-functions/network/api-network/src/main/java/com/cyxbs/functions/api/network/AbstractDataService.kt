package com.cyxbs.functions.api.network

import com.cyxbs.components.router.ServiceManager
import com.cyxbs.functions.api.network.internal.IRequestService
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx3.asObservable

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/25 20:00
 */
abstract class AbstractDataService(
  val parameters: LinkedHashMap<String, String>,
  val output: String, // 应该返回的数据格式，建议写成 json
) {

  private val mSourceService = ServiceManager(IRequestService::class)

  /**
   * @param isForce 是否强制重新请求，如果不强制，则在未到更新间时隔会返回缓存值
   */
  fun request(isForce: Boolean, vararg values: String): Single<String> {
    if (values.size != parameters.size) {
      throw IllegalArgumentException("参数个数不对应，应有 ${parameters.size}, 实有: ${values.size}")
    }
    return flow {
      emit(mSourceService.request(this@AbstractDataService, isForce, values.toList()))
    }.asObservable()
      .singleOrError()
  }
}