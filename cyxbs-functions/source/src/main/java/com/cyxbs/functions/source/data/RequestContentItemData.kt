package com.cyxbs.functions.source.data

import com.cyxbs.functions.source.room.entity.RequestContentEntity
import com.cyxbs.functions.source.room.entity.RequestItemEntity
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