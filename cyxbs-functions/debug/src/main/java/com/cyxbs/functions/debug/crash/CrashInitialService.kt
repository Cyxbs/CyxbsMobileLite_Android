package com.cyxbs.functions.debug.crash

import com.cyxbs.components.init.IInitialManager
import com.cyxbs.components.init.IInitialService
import com.g985892345.provider.api.annotation.ImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2022/9/23 15:51
 */
@ImplProvider
object CrashInitialService : IInitialService {
  
  override fun onMainProcess(manager: IInitialManager) {
    android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
      "onMainProcess")
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
        "throwable = $throwable")
      CrashActivity.start(
        throwable,
        manager.currentProcessName,
        thread.name
      )
    }
  }
  
  override fun onOtherProcess(manager: IInitialManager) {
    android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
      "onOtherProcess")
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
        "onOtherProcess: setDefaultUncaughtExceptionHandler")
      CrashActivity.start(
        throwable,
        manager.currentProcessName,
        thread.name
      )
    }
  }
}