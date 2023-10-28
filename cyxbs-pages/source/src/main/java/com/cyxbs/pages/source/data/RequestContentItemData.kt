package com.cyxbs.pages.source.data

import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.cyxbs.pages.source.room.entity.RequestItemEntity
import java.io.Serializable

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:27
 */
data class RequestContentItemData(
  val item: RequestItemEntity,
  val content: RequestContentEntity,
) : Serializable {
}