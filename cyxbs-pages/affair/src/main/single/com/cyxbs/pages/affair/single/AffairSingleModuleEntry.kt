package com.cyxbs.pages.affair.single

import android.content.Context
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.g985892345.provider.annotation.SingleImplProvider

@SingleImplProvider
object AffairSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(context: Context): ISingleModuleEntry.Page {
    throw NotImplementedError()
  }
}