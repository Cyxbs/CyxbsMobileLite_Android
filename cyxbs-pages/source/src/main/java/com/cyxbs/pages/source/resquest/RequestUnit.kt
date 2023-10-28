package com.cyxbs.pages.source.resquest

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Keep
import com.g985892345.android.extensions.android.mainHandler
import com.g985892345.android.extensions.android.postDelay
import com.g985892345.android.utils.context.appContext
import kotlinx.coroutines.CancellableContinuation
import com.g985892345.android.extensions.android.toast as toast2
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resumeWithException

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 23:57
 */
class RequestUnit(
  val enableToast: Boolean = false,
  val timeout: Int = 5,
) {

  private val mWebView by lazy {
    WebView(appContext).apply {
      // 支持 js
      @SuppressLint("SetJavaScriptEnabled")
      settings.javaScriptEnabled = true
      addJavascriptInterface(mAndroidBridge, "androidBridge")
    }
  }

  private var mContinuation: CancellableContinuation<String>? = null

  private var mTimeoutRunnable = Runnable {
    mContinuation?.let {
      clearWebView()
      it.resumeWithException(TimeoutException("超时，" +
          "你的 js 在 $timeout 秒内调用 androidBridge.success('...') 返回结果了吗 ?"))
      mContinuation = null
    }
  }

  private val mAndroidBridge = Android2JsBridge { result ->
    mContinuation?.let {
      mainHandler.removeCallbacks(mTimeoutRunnable)
      mainHandler.post {
        clearWebView()
        it.resumeWith(result)
        mContinuation = null
      }
    }
  }

  suspend fun load(
    url: String?,
    js: String?,
  ): String = suspendCancellableCoroutine { continuation ->
    if (url == null && js == null) {
      continuation.resumeWithException(IllegalArgumentException("url 和 js 不能都为 null"))
      return@suspendCancellableCoroutine
    }
    mContinuation = continuation
    mainHandler.postDelay(timeout * 1000L, mTimeoutRunnable)
    continuation.invokeOnCancellation {
      mContinuation = null
      mainHandler.removeCallbacks(mTimeoutRunnable)
      mainHandler.post {
        clearWebView()
      }
    }
    if (url != null) {
      loadUrl(url, js)
    } else if (js != null) {
      loadJs(js)
    }
  }

  private fun loadUrl(url: String, js: String?) {
    mainHandler.post {
      mWebView.webViewClient = object : RequestWebViewClient(url, { mAndroidBridge.error(it) }) {
        override fun onPageFinished(view: WebView, url: String) {
          super.onPageFinished(view, url)
          if (js != null) {
            view.evaluateJavascript(js, null)
          } else {
            view.evaluateJavascript("""
              androidBridge.success(document.body.textContent);
            """.trimIndent(), null)
          }
        }
      }
      mWebView.loadUrl(url)
    }
  }

  private fun loadJs(js: String) {
    mainHandler.post {
      mWebView.webViewClient = mEmptyWebClient
      mWebView.evaluateJavascript(js, null)
    }
  }

  private val mEmptyWebClient = WebViewClient()

  private fun clearWebView() {
    mWebView.webViewClient = mEmptyWebClient
  }

  @Keep
  private inner class Android2JsBridge(
    val callback: (result: Result<String>) -> Unit
  ) {

    @JavascriptInterface
    fun success(result: String) {
      callback.invoke(Result.success(result))
    }

    @JavascriptInterface
    fun error(result: String) {
      callback.invoke(Result.failure(RuntimeException(result)))
    }

    @JavascriptInterface
    fun toast(s: String) {
      if (enableToast) {
        toast2(s)
      }
    }
  }

  private open class RequestWebViewClient(
    val url: String?,
    val errorCallback: (String) -> Unit
  ) : WebViewClient() {
    override fun onReceivedError(
      view: WebView,
      request: WebResourceRequest,
      error: WebResourceError
    ) {
      super.onReceivedError(view, request, error)
      if (url == request.url.toString()) {
        errorCallback.invoke("errorCode=${error.errorCode}, description=${error.description}")
      }
    }

    override fun onReceivedHttpError(
      view: WebView,
      request: WebResourceRequest,
      errorResponse: WebResourceResponse
    ) {
      super.onReceivedHttpError(view, request, errorResponse)
      if (url == request.url.toString()) {
        errorCallback.invoke("statusCode=${errorResponse.statusCode}")
      }
    }
  }
}