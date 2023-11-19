package com.cyxbs.components.script.py.service

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.cyxbs.components.script.py.IPythonService
import com.g985892345.android.utils.context.appContext
import com.g985892345.provider.annotation.ImplProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 00:04
 */
@ImplProvider
object PythonServiceImpl : IPythonService {

  override suspend fun evaluatePy(py: String): String =
    suspendCancellableCoroutine { continuation->
      if (!Python.isStarted()) {
        Python.start(AndroidPlatform(appContext))
      }
      val runPyModule = Python.getInstance().getModule("run_py")
      continuation.invokeOnCancellation {
        //当请求超时协程取消，停止py程序运行
        runPyModule.callAttr("cancel")
      }
      continuation.resume(runPyModule.callAttr("getByPyScript", py).toString())
    }
}