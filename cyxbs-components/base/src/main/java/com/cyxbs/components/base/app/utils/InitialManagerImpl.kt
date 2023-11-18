package com.cyxbs.components.base.app.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.Process
import com.cyxbs.components.init.IInitialManager
import com.cyxbs.components.init.IInitialService
import com.cyxbs.components.router.ServiceManager

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/25 21:05
 */
class InitialManagerImpl(
  override val application: Application
) : IInitialManager {

  fun init() {
    val allImpl = ServiceManager.getAllImpl(IInitialService::class)
      .map { it.value.get() }
    if (isMainProcess) {
      allImpl.forEach { it.onMainProcess(this) }
    } else {
      allImpl.forEach { it.onOtherProcess(this) }
    }
  }

  // https://cloud.tencent.com/developer/article/1708529
  override val currentProcessName: String by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      Application.getProcessName()
    } else {
      try {
        // Android 9 之前无反射限制
        @SuppressLint("PrivateApi")
        val declaredMethod = Class
          .forName("android.app.ActivityThread", false, Application::class.java.classLoader)
          .getDeclaredMethod("currentProcessName")
        declaredMethod.isAccessible = true
        declaredMethod.invoke(null) as String
      } catch (e: Throwable) {
        (application.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager)
          .runningAppProcesses
          .first {
            it.pid == Process.myPid()
          }.processName
      }
    }
  }
}