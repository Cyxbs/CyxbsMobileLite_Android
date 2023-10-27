package com.cyxbs.functions.api.network

import okhttp3.OkHttpClient

/**
 * 用于接收 OkHttp 创建时的回调，提供给其他模块使用
 *
 * 注意：只支持 @SingleImplProvider 注解
 *
 * @author 985892345
 * @date 2023/10/19 00:28
 */
interface IOkHttpService {

  /**
   * 创建应用主 Retrofit 时的回调
   */
  fun onCreateMainOkHttp(builder: OkHttpClient.Builder) {}

  /**
   * 创建自定义 Retrofit 时的回调
   */
  fun onCreateCustomOkHttp(builder: OkHttpClient.Builder) {}
}