package com.cyxbs.functions.network

import com.cyxbs.functions.api.network.INetworkService
import com.cyxbs.functions.api.network.utils.getBaseUrl
import com.g985892345.provider.annotation.SingleImplProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 22:30
 */
@SingleImplProvider(INetworkService::class)
object NetworkServiceImpl : INetworkService {
  
  private val retrofit = createRetrofit(true)
  
  override fun <T : Any> getApiService(clazz: KClass<T>): T {
    return retrofit.create(clazz.java)
  }

  override fun createRetrofit(
    isNeedCookie: Boolean,
    config: (OkHttpClient.Builder.() -> Unit)?
  ): Retrofit {
    return Retrofit
      .Builder()
      .baseUrl(getBaseUrl())
      .client(OkHttpClient().newBuilder().run {
        config?.invoke(this)
        defaultOkhttpConfig(isNeedCookie)
      })
      // https://github.com/square/retrofit/tree/master/retrofit-converters/kotlinx-serialization
      .addConverterFactory(Json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
      .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
      .build()
  }
  
  private fun OkHttpClient.Builder.defaultOkhttpConfig(isNeedCookie: Boolean): OkHttpClient {
    connectTimeout(10, TimeUnit.SECONDS)
    readTimeout(10, TimeUnit.SECONDS)
    addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    return build()
  }
}