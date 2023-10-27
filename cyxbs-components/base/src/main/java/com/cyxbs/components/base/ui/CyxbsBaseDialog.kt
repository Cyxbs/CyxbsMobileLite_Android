package com.cyxbs.components.base.ui

import android.content.Context
import androidx.annotation.StyleRes
import com.g985892345.android.base.ui.page.GxrBaseDialog

/**
 * 如果只是一般的单按钮和双按钮 dialog，请使用封装好的 ChooseDialog
 *
 * @author 985892345
 * @date 2023/10/26 19:23
 */
abstract class CyxbsBaseDialog : GxrBaseDialog {

  constructor(context: Context) : super(context)

  constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)

}