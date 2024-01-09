package com.cyxbs.components.singlemodule.app

import android.util.Log
import com.cyxbs.components.base.app.CyxbsBaseApp
import com.g985892345.android.extensions.android.toastLong
import com.g985892345.provider.api.init.KtProviderInitializer

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/16 22:39
 */
class SingleModuleApp : CyxbsBaseApp() {

  companion object {
    private const val TAG = "SingleModuleApp"

    internal var RouterException: Exception? = null
  }

  override fun initRouter() {
    try {
      @Suppress("UNCHECKED_CAST")
      val ktProviderEntries = Class.forName("KtProviderEntriesKt")
        .getDeclaredMethod("get_ktProviderEntries")
       .invoke(null) as Array<String>
      Log.d(TAG, "ktProviderEntries = \n${ktProviderEntries.joinToString("\n")}")
      ktProviderEntries.forEach {
        val ktProviderInitializer = Class.forName(it)
          .getField("INSTANCE")
          .get(null) as KtProviderInitializer
        ktProviderInitializer.tryInitKtProvider()
        Log.d(TAG, "ktProviderInitializer = $ktProviderInitializer")
      }
    } catch (e: Exception) {
      toastLong("单模块初始化失败\n详细请查看 log")
      Log.e(TAG, "单模块初始化失败，可能是因为开启单模块调试的模块未生成 KtProviderInitializer 实现类\n" +
          "请联系 @985892345 进行维护")
      Log.e(TAG, e.stackTraceToString())
      RouterException = e
    }
  }
}