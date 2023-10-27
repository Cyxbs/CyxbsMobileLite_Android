package com.cyxbs.components.init

import android.app.Application

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/25 21:04
 */
interface IInitialManager {

  val application: Application

  val currentProcessName: String

  val packageName: String

  val isMainProcess: Boolean
    get() = currentProcessName == packageName
}