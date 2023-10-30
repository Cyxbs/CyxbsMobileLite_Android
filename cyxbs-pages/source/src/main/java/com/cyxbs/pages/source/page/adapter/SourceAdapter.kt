package com.cyxbs.pages.source.page.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.data.RequestItemData
import com.cyxbs.pages.source.page.content.ItemContentsActivity
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.setOnSingleClickListener

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/19 09:13
 */
class SourceAdapter : ListAdapter<RequestItemData, SourceAdapter.SourceVH>(
  object : DiffUtil.ItemCallback<RequestItemData>() {
    override fun areItemsTheSame(oldItem: RequestItemData, newItem: RequestItemData): Boolean {
      return oldItem.item.name == newItem.item.name
    }

    override fun areContentsTheSame(oldItem: RequestItemData, newItem: RequestItemData): Boolean {
      // 根据使用情况来决定内容是否改变，没必要直接比对全部数据
      return oldItem.item.sort == newItem.item.sort
          && oldItem.item.isSuccess == newItem.item.isSuccess
          && oldItem.item.requestTimestamp == newItem.item.requestTimestamp
    }
  }
) {

  inner class SourceVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.source_tv_list_item_source_name)
    val tvState: TextView = itemView.findViewById(R.id.source_tv_list_item_source_state)
    val tvRequestTime: TextView = itemView.findViewById(R.id.source_tv_list_item_source_request_time)

    init {
      itemView.setOnSingleClickListener {
        ItemContentsActivity.start(it.context, getItem(layoutPosition).item.name)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceVH {
    return LayoutInflater.from(parent.context)
      .inflate(R.layout.source_list_item_request, parent, false)
      .let { SourceVH(it) }
  }

  override fun onBindViewHolder(holder: SourceVH, position: Int) {
    val data = getItem(position)
    setState(holder, data)
    setRequest(holder, data)
    holder.apply {
      tvName.text = data.item.name
    }
  }

  private fun setState(holder: SourceVH, data: RequestItemData) {
    holder.apply {
      if (data.item.sort.isEmpty()) {
        tvState.text = "未设置"
        tvState.setTextColor(android.R.color.holo_orange_dark.color)
      } else {
        when (data.item.isSuccess) {
          true -> {
            tvState.text = "成功"
            tvState.setTextColor(android.R.color.holo_green_dark.color)
          }
          false -> {
            tvState.text = "失败"
            tvState.setTextColor(android.R.color.holo_red_dark.color)
          }
          null -> {
            tvState.text = "未请求"
            tvState.setTextColor(android.R.color.holo_blue_dark.color)
          }
        }
      }
    }
  }

  @SuppressLint("SetTextI18n")
  private fun setRequest(holder: SourceVH, data: RequestItemData) {
    holder.apply {
      if (data.item.requestTimestamp == null) {
        tvRequestTime.text = "未请求过"
        return
      }
      val diff = System.currentTimeMillis() - data.item.requestTimestamp
      if (diff / 1000 < 60) {
        tvRequestTime.text = "刚刚已请求"
        return
      }
      val minute = diff / 1000 / 60
      val show = when {
        minute < 60 -> "$minute 分钟"
        minute < 24 * 60 -> "${minute / 60} 小时 ${minute % 60} 分钟"
        else -> "${minute / 60 / 24} 天"
      }
      tvRequestTime.text = "${show}前请求"
    }
  }
}