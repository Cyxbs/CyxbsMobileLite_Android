package com.cyxbs.pages.api.source

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.cyxbs.api.source.R
import com.cyxbs.components.view.view.ScaleScrollEditText

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/18 16:11
 */
interface IDataSourceService {

  val drawable: Drawable

  /**
   * @param data 自定义保存的数据，由子类自己实现，会存进数据库，可以保存代码内容。如果为空，则说明之前没有设置数据
   * @param view 代码编辑框
   * @param parent header 被添加进的父布局
   * @return 返回 header，请使用 [R.layout.source_item_code_header]
   * 一般用于提供一些特殊设置，比如 WebView 支持单独设置 url，其他纯脚本时可以不用设置 header
   */
  fun config(data: String?, view: ScaleScrollEditText, parent: ViewGroup): List<View>

  /**
   * @return 返回自定义保存的数据，返回 null 时说明不能创建数据，建议此时 toast 原因
   */
  fun createData(view: ScaleScrollEditText, header: List<View>): String?

  /**
   * 进行请求
   * @param data 自定义保存的数据，由子类自己实现，会存进数据库，可以保存代码内容
   * @param parameterWithValue 参数名字与值
   */
  suspend fun request(data: String, parameterWithValue: Map<String, String>): String
}