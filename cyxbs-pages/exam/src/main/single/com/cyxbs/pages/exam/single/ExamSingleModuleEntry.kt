package com.cyxbs.pages.exam.single

import android.content.Context
import android.content.Intent
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.cyxbs.pages.exam.ui.ExamActivity
import com.g985892345.provider.annotation.ImplProvider

@ImplProvider
object ExamSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(context: Context): ISingleModuleEntry.Page {

    return ISingleModuleEntry.ActivityPage(
      Intent(context, ExamActivity::class.java)
    )
  }
}