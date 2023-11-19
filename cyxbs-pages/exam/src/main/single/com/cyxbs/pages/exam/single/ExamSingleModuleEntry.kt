package com.cyxbs.pages.exam.single

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import com.cyxbs.components.singlemodule.ISingleModuleEntry
import com.cyxbs.pages.exam.R
import com.cyxbs.pages.exam.single.dialog.AccountDialog
import com.cyxbs.pages.exam.ui.ExamActivity
import com.g985892345.android.extensions.android.cast
import com.g985892345.android.extensions.android.drawable
import com.g985892345.android.extensions.android.mainHandler
import com.g985892345.android.extensions.android.postDelay
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.size.dp2px
import com.g985892345.android.extensions.android.size.dp2pxF
import com.g985892345.android.utils.context.application
import com.g985892345.android.utils.impl.impl.ActivityLifecycleCallbacksImpl
import com.g985892345.provider.annotation.ImplProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton

@ImplProvider
object ExamSingleModuleEntry : ISingleModuleEntry {

  override fun getPage(context: Context): ISingleModuleEntry.Page {
    registerActivityCallback()
    return ISingleModuleEntry.ActivityPage(
      Intent(context, ExamActivity::class.java)
    )
  }

  private fun registerActivityCallback() {
    application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksImpl  {
      override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityPostCreated(activity, savedInstanceState)
        if (activity::class == ExamActivity::class) {
          activity.window.decorView.cast<ViewGroup>()
            .addView(
              FloatingActionButton(activity).apply {
                setImageDrawable(R.drawable.exam_baseline_settings_24.drawable)
                setOnSingleClickListener {
                  AccountDialog.Builder(it.context)
                    .show()
                }
              },
              FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.END
              ).apply {
                bottomMargin = 60.dp2px
                marginEnd = 40.dp2px
              }
            )
        }
      }
    })
  }
}