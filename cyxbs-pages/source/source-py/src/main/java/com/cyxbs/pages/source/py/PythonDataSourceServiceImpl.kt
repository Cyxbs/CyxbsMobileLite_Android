package com.cyxbs.pages.source.py

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.script.py.IPythonService
import com.cyxbs.components.view.view.ScaleScrollEditText
import com.cyxbs.pages.api.source.IDataSourceService
import com.g985892345.android.extensions.android.drawable
import com.g985892345.android.extensions.android.toast
import com.g985892345.provider.annotation.ImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 00:19
 */
@ImplProvider(IDataSourceService::class, "Python")
internal object PythonDataSourceServiceImpl : IDataSourceService {

  override val drawable: Drawable = R.drawable.py_ic_python.drawable

  override fun config(
    data: String,
    view: ScaleScrollEditText,
    parent: ViewGroup
  ): List<View> {
    view.hint = """
      请输入 py
      
      端上传递请求参数规则:
        端上可以传递参数到 py 代码中
        引用规则: 
          以 {TEXT} 的方式进行引用, 在请求前会进行字符串替换
        但请注意这只是简单的替换字符串
        
      该面板支持双指放大缩小
    """.trimIndent()
    if (data.isNotBlank()) {
      view.text = data
    }
    return emptyList()
  }

  override fun createData(
    view: ScaleScrollEditText,
    header: List<View>
  ): String? {
    if (view.text.isNullOrBlank()) {
      toast("python 代码不能为空")
      return null
    }
    return view.text.toString()
  }

  override suspend fun request(data: String, parameterWithValue: Map<String, String>): String {
    return ServiceManager.getImplOrThrow(IPythonService::class)
      .evaluatePy(data.replaceValue(parameterWithValue))
  }

  private fun String.replaceValue(
    parameterWithValue: Map<String, String>
  ): String {
    if (parameterWithValue.isEmpty()) return this
    var result = this
    parameterWithValue.forEach {
      result = result.replace("{${it.key}}", it.value)
    }
    return result
  }
}