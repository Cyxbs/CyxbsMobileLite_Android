package com.cyxbs.components.script.webview.service

import com.cyxbs.components.script.webview.IWebViewSpiderService
import com.cyxbs.components.script.webview.webview.WebViewRequest
import com.g985892345.provider.annotation.ImplProvider
import kotlinx.coroutines.channels.Channel

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/31 08:09
 */
@ImplProvider
internal object WebViewSpiderServiceImpl : IWebViewSpiderService {

  private const val CAPACITY = 3

  private val mWaitChanel = Channel<WebViewRequest>(CAPACITY).apply {
    repeat(CAPACITY) {
      trySend(WebViewRequest())
    }
  }

  override suspend fun load(url: String): String {
    return mWaitChanel.receive().load(url, null)
  }

  override suspend fun load(url: String, js: String): String {
    return mWaitChanel.receive().load(url, js)
  }

  override suspend fun evaluateJs(js: String): String {
    return mWaitChanel.receive().load(null, js)
  }
}