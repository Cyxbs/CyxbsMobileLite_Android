package com.cyxbs.components.base.app

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import com.cyxbs.components.base.app.utils.InitialManagerImpl
import com.cyxbs.components.router.ServiceManager
import com.g985892345.android.extensions.android.mainHandler

/**
 * .
 *
 * @author 985892345
 * @date 2023/9/6 23:58
 */
abstract class CyxbsBaseApp : Application() {

  override fun onCreate() {
    super.onCreate()
    initRouter()
    initInitialManager()
  }

  abstract fun initRouter()

  private fun initInitialManager() {
    InitialManagerImpl(this).init()
  }
}