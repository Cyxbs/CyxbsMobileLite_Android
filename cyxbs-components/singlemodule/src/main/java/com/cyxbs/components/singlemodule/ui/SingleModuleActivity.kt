package com.cyxbs.components.singlemodule.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.extensions.android.mainHandler
import com.g985892345.android.extensions.android.postDelay

/**
 * 用于单模块调试启动的 activity
 *
 * @author 985892345
 * @date 2023/9/7 00:13
 */
class SingleModuleActivity : CyxbsBaseActivity() {

  private val mSingleModuleEntry by lazyUnlock {
    ServiceManager.getImplOrNull(ISingleModuleEntry::class)
  }

  override val isCancelStatusBar: Boolean
    get() = mSingleModuleEntry?.isCancelStatusBar ?: super.isCancelStatusBar

  override val isPortraitScreen: Boolean
    get() = mSingleModuleEntry?.isPortraitScreen ?: super.isPortraitScreen

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    when (val page = mSingleModuleEntry?.getPage(this)) {
      is ISingleModuleEntry.ActivityPage -> {
        startActivity(page.intent)
        mainHandler.postDelay(1000) {
          // 防止另一个 activity 还没有完全打开
          finish()
        }
      }
      is ISingleModuleEntry.FragmentPage -> {
        replaceFragment(android.R.id.content) { page.fragment.invoke() }
      }
      null -> {
        setContentView(
          FrameLayout(this).also { layout ->
            layout.addView(TextView(this).apply {
              text = "单模块加载失败\n未找到对应的 ISingleModuleEntry 实现类\n" +
                  "请联系 @985892345 进行维护"
              gravity = Gravity.CENTER
            }, FrameLayout.LayoutParams(
              FrameLayout.LayoutParams.WRAP_CONTENT,
              FrameLayout.LayoutParams.WRAP_CONTENT,
              Gravity.CENTER
            ))
          }
        )
      }
    }
  }
}