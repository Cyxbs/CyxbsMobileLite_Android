package com.cyxbs.pages.main.single

import android.content.Context
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.g985892345.provider.api.annotation.ImplProvider

@ImplProvider
object MainSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(context: Context): ISingleModuleEntry.Page {
    throw NotImplementedError()
  }
}