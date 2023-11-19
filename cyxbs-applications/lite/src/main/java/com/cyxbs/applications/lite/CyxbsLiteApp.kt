package com.cyxbs.applications.lite

import com.cyxbs.components.base.app.CyxbsBaseApp
import com.g985892345.provider.cyxbsmobileliteandroid.cyxbsapplications.lite.LiteKtProviderInitializer

/**
 * .
 *
 * @author 985892345
 * 2023/9/6 11:36
 */
class CyxbsLiteApp : CyxbsBaseApp() {
  override fun initRouter() {
    LiteKtProviderInitializer.tryInitKtProvider()
  }
}