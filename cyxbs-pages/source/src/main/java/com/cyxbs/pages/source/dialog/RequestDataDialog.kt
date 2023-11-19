package com.cyxbs.pages.source.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.cyxbs.components.view.R
import com.cyxbs.components.view.dialog.ChooseDialog
import com.cyxbs.components.view.view.ScaleScrollTextView
import com.cyxbs.pages.source.data.RequestContentItemData
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.size.dp2px
import com.g985892345.android.extensions.android.toast

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 22:49
 */
class RequestDataDialog private constructor(
  context: Context,
  val itemData: RequestContentItemData
) : ChooseDialog(context) {

  class Builder(
    context: Context,
    val itemData: RequestContentItemData
  ) : ChooseDialog.Builder(
    context,
    DataImpl(
      positiveButtonText = "关闭",
      negativeButtonText = "关闭",
      buttonWidth = 110,
      buttonHeight = 38,
      width = 320,
      height = 500
    ),
  ) {
    override fun buildInternal(): RequestDataDialog {
      setPositiveClick {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("json", data.content))
        toast("复制成功！")
      }.setNegativeClick {
        dismiss()
      }
      return RequestDataDialog(context, itemData)
    }
  }

  private val mTvExceptionMessage = TextView(context).apply {
    layoutParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.WRAP_CONTENT,
    ).apply {
      leftMargin = 10.dp2px
    }
    textSize = 12F
    setTextIsSelectable(true)
  }

  private val mScaleScrollTextView = ScaleScrollTextView(context, null).apply {
    layoutParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.MATCH_PARENT,
    ).apply {
      topMargin = 10.dp2px
    }
    setSideBackgroundColor(R.color.view_dialog_bg.color)
  }

  private val mLinearLayout = LinearLayout(context).apply {
    layoutParams = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.MATCH_PARENT,
      Gravity.CENTER
    ).apply {
      topMargin = 15.dp2px
      bottomMargin = topMargin
      leftMargin = 8.dp2px
      rightMargin = 16.dp2px
    }
    orientation = LinearLayout.VERTICAL
    addView(mTvExceptionMessage)
    addView(mScaleScrollTextView)
  }

  override fun createContentView(parent: ViewGroup): View {
    return mLinearLayout
  }

  override fun initContentView(view: View) {
    mScaleScrollTextView.text = itemData.content.response
    mTvExceptionMessage.text = itemData.content.title
    view.post {
      mScaleScrollTextView.maxTextWidth = mScaleScrollTextView.width
    }
  }
}