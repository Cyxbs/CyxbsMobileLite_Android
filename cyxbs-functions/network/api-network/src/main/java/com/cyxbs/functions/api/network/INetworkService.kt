package com.cyxbs.functions.api.network

import com.cyxbs.components.router.IService
import com.cyxbs.components.router.ServiceManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.reflect.KClass

/**
 * # 用法
 * ## 命名规则
 * XXXApiService 接口，命名规则，以 ApiService 结尾
 *
 * ## 接口模板
 * ```
 * interface XXXApiService {
 *
 *     @GET("/aaa/bbb")
 *     fun getXXX(): Single<ApiWrapper<Bean>>
 *     // 统一使用 ApiWrapper 或 ApiStatus 包装，注意 Bean 类要去掉 data 字段，不然会报 json 错误
 *
 *     companion object {
 *         val INSTANCE by lazy {
 *             INetworkService::class.impl
 *                 .getXXXApiService(XXXApiService::class)
 *         }
 *     }
 * }
 *
 * 或者：
 * interface XXXApiService : IApi
 *
 * XXXApiService::class.api
 *     .getXXX()
 * ```
 *
 * ## 示例代码
 * ```
 * ApiService.INSTANCE.getXXX()
 *     .subscribeOn(Schedulers.io())  // 线程切换
 *     .observeOn(AndroidSchedulers.mainThread())
 *     .mapOrInterceptException {     // 当 errorCode 的值不为成功时抛错，并拦截异常
 *         // 这里面可以使用 DSL 写法，更多详细用法请看该方法注释
 *     }
 *     .safeSubscribeBy {            // 如果是网络连接错误，则这里会默认处理
 *         // 成功的时候
 *         // 如果是仓库层，请使用 unsafeSubscribeBy()
 *     }
 * ```
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 22:30
 */
interface INetworkService : IService  {

  fun <T : Any> getApiService(clazz: KClass<T>): T

  /**
   * 创建自定义的 Retrofit
   *
   * 注意保存该 Retrofit 对象，而不是每次都创建新的
   */
  fun createRetrofit(
    isNeedCookie: Boolean,
    config: (OkHttpClient.Builder.() -> Unit)? = null
  ): Retrofit
}

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