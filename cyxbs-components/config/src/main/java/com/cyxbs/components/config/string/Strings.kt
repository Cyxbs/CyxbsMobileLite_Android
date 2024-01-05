package com.cyxbs.components.config.string

import android.content.ComponentCallbacks
import android.content.res.Configuration
import androidx.annotation.ArrayRes
import com.cyxbs.components.config.R
import com.g985892345.android.utils.context.appContext
import com.g985892345.android.utils.context.application

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/2 16:07
 */
object Strings {

  fun getWeekStr(num: Int): String = mWeekStrArray[num]
  fun getWeekNumStr(num: Int): String = mWeekNumStrArray[num]
  fun getMonthStr(num: Int): String = mMonthStrArray[num]
  fun getMonthNumStr(num: Int): String = mMonthNumStrArray[num]

  init {
    initArray()
    application.registerComponentCallbacks(object : ComponentCallbacks {
      override fun onConfigurationChanged(newConfig: Configuration) {
        // 配置发生改变，重新初始化 string
        initArray()
      }

      override fun onLowMemory() {
      }
    })
  }

  private val mWeekStrArray = mutableListOf<String>()
  private val mWeekNumStrArray = mutableListOf<String>()
  private val mMonthStrArray = mutableListOf<String>()
  private val mMonthNumStrArray = mutableListOf<String>()

  private fun initArray() {
    initList(mWeekStrArray, R.array.config_week_array)
    initList(mWeekNumStrArray, R.array.config_week_num_array)
    initList(mMonthStrArray, R.array.config_month_array)
    initList(mMonthNumStrArray, R.array.config_month_num_array)
  }

  private fun initList(list: MutableList<String>, @ArrayRes arrayId: Int) {
    list.clear()
    list.addAll(appContext.resources.getStringArray(arrayId))
  }
}