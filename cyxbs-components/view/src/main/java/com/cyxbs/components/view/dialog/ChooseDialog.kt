package com.cyxbs.components.view.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.size.dp2px

/**
 * 该 dialog 为通用 dialog，包含一个按钮或两个按钮
 *
 * @author 985892345
 * @date 2023/10/26 19:51
 */
open class ChooseDialog : BaseChooseDialog<ChooseDialog, ChooseDialog.Data> {

  protected constructor(context: Context) : super(context)

  protected constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)

  /**
   * 可以使用 [DataImpl]
   */
  interface Data : BaseChooseDialog.Data {
    val content: CharSequence
      get() = "弹窗内容为空"
    val contentSize: Float
      get() = 13F
    val contentGravity: Int
      get() = Gravity.CENTER

    companion object : Data
  }

  data class DataImpl(
    override val content: CharSequence = Data.content,
    override val contentSize: Float = Data.contentSize,
    override val contentGravity: Int = Data.contentGravity,
    override val type: DialogType = Data.type,
    override val positiveButtonText: String = Data.positiveButtonText,
    override val negativeButtonText: String = Data.negativeButtonText,
    override val positiveButtonColor: Int = Data.positiveButtonColor,
    override val negativeButtonColor: Int = Data.negativeButtonColor,
    override val buttonWidth: Int = Data.buttonWidth,
    override val buttonHeight: Int = Data.buttonHeight,
    override val width: Int = Data.width,
    override val height: Int = Data.height,
    override val backgroundId: Int = Data.backgroundId,
  ) : Data

  open class Builder(
    context: Context,
    data: Data
  ) : BaseChooseDialog.Builder<ChooseDialog, Data>(context, data) {
    override fun buildInternal(): ChooseDialog {
      return ChooseDialog(context)
    }
  }

  override fun createContentView(parent: ViewGroup): View {
    return TextView(parent.context).apply {
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
        Gravity.CENTER
      ).apply {
        topMargin = 15.dp2px
        bottomMargin = topMargin
        leftMargin = 25.dp2px
        rightMargin = leftMargin
      }
      setTextColor(com.cyxbs.components.config.R.color.config_level_four_font_color.color)
    }
  }

  override fun initContentView(view: View) {
    view as TextView
    view.text = data.content
    view.textSize = data.contentSize
    view.gravity = data.contentGravity
  }
}