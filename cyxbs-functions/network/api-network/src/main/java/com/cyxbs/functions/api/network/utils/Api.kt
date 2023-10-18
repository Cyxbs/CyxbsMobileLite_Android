package com.cyxbs.functions.api.network.utils

import com.g985892345.android.extensions.android.isDebuggableBuild


/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/29 22:33
 */

const val DEBUG_URL = "https://www.wanandroid.com"
const val RELEASE_URL = DEBUG_URL

fun getBaseUrl() = if (isDebuggableBuild) DEBUG_URL else RELEASE_URL