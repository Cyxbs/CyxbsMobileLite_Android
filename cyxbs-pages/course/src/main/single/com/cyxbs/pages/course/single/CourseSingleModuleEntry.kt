package com.cyxbs.pages.course.single

import android.content.Context
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.cyxbs.pages.course.TestFragment
import com.g985892345.provider.annotation.ImplProvider

@ImplProvider
object CourseSingleModuleEntry : ISingleModuleEntry {

  override fun getPage(context: Context): ISingleModuleEntry.Page {
    return ISingleModuleEntry.FragmentPage {
      TestFragment()
    }
  }
}