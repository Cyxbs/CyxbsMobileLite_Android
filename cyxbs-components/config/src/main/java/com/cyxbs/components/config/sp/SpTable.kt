package com.cyxbs.components.config.sp

import android.content.Context
import android.content.SharedPreferences
import com.g985892345.android.utils.context.appContext

/**
 * 这里面保存需要跨模块全局使用的 SP，重要数据以及大量数据请不要使用该 SP 保存
 *
 * 注意：有些数据根本就没有放在全局中使用，自己模块中使用到的 SP 请放在自己的模块中！！！
 */

/**
 * 全局通用的 Sp，用于整个应用内传递数据，重要数据以及大量数据请不要使用该 SP 保存
 */
val defaultSp: SharedPreferences
  get() = appContext.getSharedPreferences("share_data", Context.MODE_PRIVATE)

/*
* 请在下面写上传递的 key 值，以 SP_模块名_作用名 开头命名，后面还可以细分
* */

// 隐私条例是否同意
const val SP_PRIVACY_AGREED = "privacy_agreed"

// 启动 App 是否优先显示课表界面
const val SP_COURSE_SHOW_STATE = "course_show_state"