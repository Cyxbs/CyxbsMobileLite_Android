package com.cyxbs.components.script.py.service

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.cyxbs.components.script.py.IPythonService
import com.g985892345.android.utils.context.appContext
import com.g985892345.provider.annotation.ImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 00:04
 */
@ImplProvider
object PythonServiceImpl : IPythonService {

  override suspend fun evaluatePy(py: String): String {
    if (!Python.isStarted()) {
      Python.start(AndroidPlatform(appContext))
    }
    return Python.getInstance().getModule("run_py").callAttr("getByPyScript", py).toString()
  }
}