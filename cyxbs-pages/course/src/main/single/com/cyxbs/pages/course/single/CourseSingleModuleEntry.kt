package com.cyxbs.pages.course.single

import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.cyxbs.pages.course.TestFragment
import com.g985892345.provider.annotation.SingleImplProvider

@SingleImplProvider(ISingleModuleEntry::class)
object CourseSingleModuleEntry : ISingleModuleEntry {

  override fun getPage(): ISingleModuleEntry.Page {
    return ISingleModuleEntry.FragmentPage {
      TestFragment()
    }
  }
}