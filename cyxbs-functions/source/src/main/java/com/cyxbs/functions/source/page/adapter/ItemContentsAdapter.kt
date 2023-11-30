package com.cyxbs.functions.source.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.components.view.crash.CrashDialog
import com.cyxbs.functions.source.R
import com.cyxbs.functions.source.page.request.SetRequestActivity
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.gone
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.visible

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:27
 */
class ItemContentsAdapter : ListAdapter<com.cyxbs.functions.source.data.RequestContentItemData, ItemContentsAdapter.RequestContentVH>(
  object : DiffUtil.ItemCallback<com.cyxbs.functions.source.data.RequestContentItemData>() {
    override fun areItemsTheSame(
      oldItem: com.cyxbs.functions.source.data.RequestContentItemData,
      newItem: com.cyxbs.functions.source.data.RequestContentItemData
    ): Boolean {
      return oldItem.content.id == newItem.content.id
    }

    override fun areContentsTheSame(
      oldItem: com.cyxbs.functions.source.data.RequestContentItemData,
      newItem: com.cyxbs.functions.source.data.RequestContentItemData
    ): Boolean {
      // 根据使用情况来决定内容是否改变，没必要直接比对全部数据
      return oldItem.content.title == newItem.content.title
          && oldItem.content.response == newItem.content.response
          && oldItem.content.error == newItem.content.error
    }
  }
) {

  inner class RequestContentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTitle: TextView = itemView.findViewById(R.id.source_tv_list_item_contents_title)
    val tvState: TextView = itemView.findViewById(R.id.source_tv_list_item_contents_state)
    val ivError: ImageView = itemView.findViewById(R.id.source_iv_list_item_contents_error)
    val ivData: ImageView = itemView.findViewById(R.id.source_iv_list_item_contents_data)

    init {
      itemView.setOnSingleClickListener { view ->
        val itemContent = getItem(layoutPosition)
        SetRequestActivity.start(
          view.context,
          itemContent.content,
          itemContent.item.parameters,
          itemContent.item.output,
        )
      }
      ivError.setOnSingleClickListener {
        val itemContent = getItem(layoutPosition)
        CrashDialog.Builder(it.context, itemContent.content.error!!)
          .show()
      }
      ivData.setOnSingleClickListener {
        val itemContent = getItem(layoutPosition)
        com.cyxbs.functions.source.dialog.RequestDataDialog.Builder(it.context, itemContent)
          .show()
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
      ivError.gone()
      ivData.gone()
      if (data.content.response != null) {
        tvState.text = "成功"
        tvState.setTextColor(android.R.color.holo_green_dark.color)
        ivData.visible()
      } else if (data.content.error != null) {
        tvState.text = "失败"
        tvState.setTextColor(android.R.color.holo_red_dark.color)
        ivError.visible()
      } else {
        tvState.text = "未请求"
        tvState.setTextColor(android.R.color.holo_blue_dark.color)
      }
    }
  }
}