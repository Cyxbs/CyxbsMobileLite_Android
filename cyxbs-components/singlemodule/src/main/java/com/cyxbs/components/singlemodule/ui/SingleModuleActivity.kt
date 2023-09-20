package com.cyxbs.components.singlemodule.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.extensions.android.toast
import java.util.ServiceLoader

/**
 * .
 *
 * @author 985892345
 * 2023/9/7 00:13
 */
class SingleModuleActivity : CyxbsBaseActivity() {

  private val mSingleModuleEntry by lazyUnlock {
    ServiceLoader.load(ISingleModuleEntry::class.java).single()
  }

  override val isCancelStatusBar: Boolean
    get() = mSingleModuleEntry.isCancelStatusBar

  override val isPortraitScreen: Boolean
    get() = mSingleModuleEntry.isPortraitScreen

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    when (val page = mSingleModuleEntry.getPage()) {
      is Intent -> startActivity(page)
      is Fragment -> replaceFragment(android.R.id.content) { page }
      else -> toast("getPage() 返回了未知类型：${page.javaClass.name}")
    }
  }
}