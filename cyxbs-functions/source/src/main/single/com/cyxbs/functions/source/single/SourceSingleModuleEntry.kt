package com.cyxbs.functions.source.single

import android.content.Context
import android.content.Intent
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.cyxbs.functions.source.page.source.SourceActivity
import com.g985892345.provider.api.annotation.ImplProvider

@ImplProvider
object SourceSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(context: Context): ISingleModuleEntry.Page {
    return ISingleModuleEntry.ActivityPage(
      Intent(context, SourceActivity::class.java)
    )
  }
}