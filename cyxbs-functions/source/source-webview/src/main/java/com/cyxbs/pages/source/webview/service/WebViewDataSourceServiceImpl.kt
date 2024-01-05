package com.cyxbs.pages.source.webview.service

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.script.webview.IWebViewSpiderService
import com.cyxbs.components.view.view.ScaleScrollEditText
import com.cyxbs.functions.source.webview.R
import com.cyxbs.pages.api.source.IDataSourceService
import com.g985892345.android.extensions.android.drawable
import com.g985892345.android.extensions.android.toast
import com.g985892345.provider.api.annotation.ImplProvider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.timeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/18 16:14
 */
@ImplProvider(clazz = IDataSourceService::class, name = "WebView")
internal object WebViewDataSourceServiceImpl : IDataSourceService {

  override val drawable: Drawable = R.drawable.webview_ic_baseline_web_24.drawable

  override fun config(data: String?, view: ScaleScrollEditText, parent: ViewGroup): List<View> {
    val webViewData = if (data.isNullOrEmpty()) {
      WebViewData(null, null)
    } else {
      Json.decodeFromString(data)
    }
    initEtJs(view, webViewData.js)
    return createHeader(parent, webViewData.url)
  }

  override fun createData(view: ScaleScrollEditText, header: List<View>): String? {
    val et = header.single().findViewById<EditText>(com.cyxbs.api.source.R.id.source_et_header)
    val url = et.text.toString().ifBlank { null }
    val js = view.text?.toString()?.ifBlank { null }
    return if (url != null || js != null) Json.encodeToString(WebViewData(url, js)) else {
      toast("url 和 js 不能都为空")
      null
    }
  }

  @OptIn(FlowPreview::class)
  override suspend fun request(data: String, parameterWithValue: Map<String, String>): String {
    val webViewDate = Json.decodeFromString<WebViewData>(data)
    val url = webViewDate.url.replaceValue(parameterWithValue)
    val js = webViewDate.js.replaceValue(parameterWithValue)
    return flow {
      val service = ServiceManager.getImplOrThrow(IWebViewSpiderService::class)
      val response = if (url != null && js != null) {
        service.load(url, js)
      } else if (url != null) {
        service.load(url)
      } else if (js != null) {
        service.evaluateJs(js)
      } else throw IllegalArgumentException("url 和 js 不能都为 null")
      emit(response)
    }.timeout(5.seconds)
      .single()
  }

  private fun String?.replaceValue(
    parameterWithValue: Map<String, String>
  ): String? {
    if (parameterWithValue.isEmpty()) return this
    var result = this ?: return null
    parameterWithValue.forEach {
      result = result.replace("{${it.key}}", it.value)
    }
    return result
  }

  private fun initEtJs(view: ScaleScrollEditText, js: String?) {
    view.hint = """
      请输入 js
      
      规则：
        1. 只填写 url，会自动获取页面文本，可用于 GET 请求
        2. 只填写 js，将直接执行，可用 js 发起 POST 请求
        2. 填写 url 和 js，则在页面加载完后执行 js
      
      与端上交互规则:
        // 调用 success() 返回结果，只允许调用一次
        cyxbsBridge.success('...'); 
        // 调用 error() 返回异常，只允许调用一次
        cyxbsBridge.error('...');
        
      端上传递请求参数规则:
        端上可以传递参数到 url 和 js 上
        引用规则: 
          以 {TEXT} 的方式进行引用, 在请求前会进行字符串替换
        例子:
          比如端上设置参数为: stu_num, 取值为: abc
          则对于如下 url: https://test/{stu_num}
          会被替换为: https://test/abc
          js 同样如此，但请注意这只是简单的替换字符串
        并不是所有请求都会有参数，是否存在参数请查看请求格式
        
      该面板支持双指放大缩小
    """.trimIndent()
    view.text = js
  }

  @SuppressLint("SetTextI18n")
  private fun createHeader(parent: ViewGroup, url: String?): List<View> {
    return listOf(
      LayoutInflater.from(parent.context)
        .inflate(com.cyxbs.api.source.R.layout.source_item_code_header, parent, false)
        .apply {
          val tv = findViewById<TextView>(com.cyxbs.api.source.R.id.source_tv_header)
          tv.text = "请求链接: "
          val et = findViewById<EditText>(com.cyxbs.api.source.R.id.source_et_header)
          et.hint = "http/https (可以只写 js)"
          et.setText(url)
        }
    )
  }

  @Serializable
  private data class WebViewData(
    val url: String?,
    val js: String?,
  )
}