package com.cyxbs.components.singlemodule.app

import android.util.Log
import com.cyxbs.components.base.app.CyxbsBaseApp
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.singlemodule.internal.IApiParentKtProvider
import com.g985892345.android.extensions.android.toastLong
import com.g985892345.provider.init.KtProviderInitializer
import ktProviderEntryClassName

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/16 22:39
 */
class SingleModuleApp : CyxbsBaseApp() {

  companion object {
    private const val TAG = "SingleModuleApp"
  }

  override fun initRouter() {
    loadKtProviderInitializer(ktProviderEntryClassName)
    // 加载 api 模块的 KtProvider 实现类
    val apiParentKtProvider = ServiceManager.getImplOrNull(IApiParentKtProvider::class)
    if (apiParentKtProvider == null) {
      toastLong("初始化 api 实现模块失效 !")
    } else {
      apiParentKtProvider.entryClassNames.forEach {
        loadKtProviderInitializer(it)
      }
    }
  }

  private fun loadKtProviderInitializer(className: String) {
    try {
      val ktProviderInitializer = Class.forName(className)
        .getField("INSTANCE")
        .get(null) as KtProviderInitializer
      ktProviderInitializer.tryInitKtProvider()
    } catch (e: Exception) {
      toastLong("单模块初始化失败\n详细请查看 log")
      Log.e(TAG, "单模块初始化失败，可能是因为开启单模块调试的模块未生成 KtProviderInitializer 实现类\n" +
          "请联系 @985892345 进行维护")
      Log.e(TAG, e.stackTraceToString())
    }
  }
}