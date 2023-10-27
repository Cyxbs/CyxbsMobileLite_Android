package com.cyxbs.pages.find.single

import android.content.Context
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.g985892345.provider.annotation.SingleImplProvider

@SingleImplProvider(ISingleModuleEntry::class)
object FindSingleModuleEntry : ISingleModuleEntry {

  override fun getPage(context: Context): ISingleModuleEntry.Page {
    throw NotImplementedError()
  }
}