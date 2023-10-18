package com.cyxbs.pages.find.single

import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.g985892345.provider.annotation.SingleImplProvider

@SingleImplProvider(ISingleModuleEntry::class)
object FindSingleModuleEntry : ISingleModuleEntry {

  override fun getPage(): ISingleModuleEntry.Page {
    TODO("返回一个启动 Activity 的 Intent 或者 Fragment")
  }
}