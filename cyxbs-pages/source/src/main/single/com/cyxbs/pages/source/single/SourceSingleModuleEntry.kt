package com.cyxbs.pages.source.single

import android.content.Context
import android.content.Intent
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.cyxbs.pages.source.page.SourceActivity
import com.g985892345.provider.annotation.SingleImplProvider

@SingleImplProvider(ISingleModuleEntry::class)
object SourceSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(context: Context): ISingleModuleEntry.Page {
    return ISingleModuleEntry.ActivityPage(
      Intent(context, SourceActivity::class.java)
    )
  }
}