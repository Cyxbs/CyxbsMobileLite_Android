package com.cyxbs.functions.api.network

import com.cyxbs.components.router.ServiceManager
import kotlin.reflect.KClass

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/19 00:10
 */
interface IApi {
  companion object {
    val MAP = HashMap<KClass<out IApi>, IApi>()
  }
}

/**
 * 带 cookie 的请求
 */
@Suppress("UNCHECKED_CAST")
val <I : IApi> KClass<I>.api: I
  get() = IApi.MAP.getOrPut(this) {
    ServiceManager(INetworkService::class).getApiService(this)
  } as I