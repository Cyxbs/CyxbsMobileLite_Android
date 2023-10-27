package com.cyxbs.pages.source.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.data.RequestContentItemData
import com.cyxbs.pages.source.page.SetRequestActivity
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.setOnSingleClickListener

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:27
 */
class ItemContentsAdapter :
  ListAdapter<RequestContentItemData, ItemContentsAdapter.RequestContentVH>(RequestContentItemData) {

  inner class RequestContentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitle: TextView = itemView.findViewById(R.id.source_tv_list_item_contents_title)
    val tvState: TextView = itemView.findViewById(R.id.source_tv_list_item_contents_state)

    init {
      itemView.setOnSingleClickListener { view ->
        val itemContent = getItem(layoutPosition)
        SetRequestActivity.start(
          view.context,
          itemContent.content,
          itemContent.item.parameters
        )
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestContentVH {
    return LayoutInflater.from(parent.context)
      .inflate(R.layout.source_list_item_contens, parent, false)
      .let { RequestContentVH(it) }
  }

  override fun onBindViewHolder(holder: RequestContentVH, position: Int) {
    val data = getItem(position)
    holder.apply {
      tvTitle.text = data.content.title
      if (data.content.response != null) {
        tvState.text = "成功"
        tvState.setTextColor(android.R.color.holo_green_dark.color)
      } else if (data.content.error != null) {
        tvState.text = "失败"
        tvState.setTextColor(android.R.color.holo_red_dark.color)
      } else {
        tvState.text = "未请求"
        tvState.setTextColor(android.R.color.holo_blue_dark.color)
      }
    }
  }
}