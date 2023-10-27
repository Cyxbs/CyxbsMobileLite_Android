package com.cyxbs.pages.source.data

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.cyxbs.pages.source.room.entity.RequestItemEntity
import java.io.Serializable

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:06
 */
data class RequestItemContentsData(
  val item: RequestItemEntity,
  val contents: List<RequestContentEntity>
) : Serializable {
  companion object : ItemCallback<RequestItemContentsData>() {
    override fun areItemsTheSame(
      oldItem: RequestItemContentsData,
      newItem: RequestItemContentsData
    ): Boolean {
      return oldItem.item.name == newItem.item.name
    }

    override fun areContentsTheSame(
      oldItem: RequestItemContentsData,
      newItem: RequestItemContentsData
    ): Boolean {
      return oldItem == newItem
    }
  }
}