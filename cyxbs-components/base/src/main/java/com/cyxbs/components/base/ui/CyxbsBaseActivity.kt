package com.cyxbs.components.base.ui

import android.app.Activity
import androidx.annotation.LayoutRes
import com.g985892345.android.base.ui.page.GxrBaseActivity
import com.g985892345.provider.annotation.KClassProvider

/**
 * 注释往父类 [GxrBaseActivity] 查看
 *
 * @author 985892345
 * @date 2023/9/6 23:55
 */
@KClassProvider(clazz = Activity::class)
abstract class CyxbsBaseActivity : GxrBaseActivity {

  constructor() : super()

  constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

}