package com.cyxbs.pages.source.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Serializable

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 14:22
 */

data class RequestItemContentsEntity(
  @Embedded
  val item: RequestItemEntity,
  @Relation(parentColumn = "name", entityColumn = "name")
  val contents: List<RequestContentEntity>,
) : Serializable

@Entity(tableName = "request_item",)
@TypeConverters(RequestEntityConverter::class)
data class RequestItemEntity(
  @PrimaryKey
  val name: String,
  val interval: Float,
  val requestTimestamp: Long?, // 每次发起请求的时间
  val responseTimestamp: Long?, // 最后请求成功时的时间戳
  val isSuccess: Boolean?, // 上一次请求是否成功, null: 请求中或者未设置请求体或者第一次设置时未请求
  val sort: List<Long>, // 请求顺序, RequestContentEntity 的 id
  val parameters: List<Pair<String, String>>, // 参数名+描述
  val output: String, // js 应该输出的格式，该格式将用于端上处理
) : Serializable

@Entity(
  tableName = "request_content",
  indices = [Index(value = ["name"])],
  foreignKeys = [
    ForeignKey(
      entity = RequestItemEntity::class,
      parentColumns = ["name"],
      childColumns = ["name"],
      onDelete = ForeignKey.CASCADE
    )
  ],
)
data class RequestContentEntity(
  val name: String,
  val title: String,
  val url: String?,
  val js: String?,
  val error: String?,
  val response: String?,
  val requestTimestamp: Long?, // 每次请求触发时的时间戳
  val responseTimestamp: Long?, // 最后请求成功时的时间戳
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0,
) : Serializable

@Entity(
  tableName = "request_cache",
  primaryKeys = ["name", "values"],
  foreignKeys = [
    ForeignKey(
      entity = RequestItemEntity::class,
      parentColumns = ["name"],
      childColumns = ["name"],
      onDelete = ForeignKey.CASCADE
    )
  ],
)
@TypeConverters(RequestEntityConverter::class)
data class RequestCacheEntity(
  val name: String,
  val values: List<String>,
  val response: String,
)

class RequestEntityConverter {
  @TypeConverter
  fun listLongToString(list: List<Long>): String {
    return list.joinToString("&")
  }
  @TypeConverter
  fun stringToListLong(string: String): List<Long> {
    if (string.isBlank()) return emptyList()
    return string.split("&").map { it.toLong() }
  }
  @TypeConverter
  fun listStringToString(list: List<String>): String {
    return list.joinToString("&")
  }
  @TypeConverter
  fun stringToListString(string: String): List<String> {
    if (string.isBlank()) return emptyList()
    return string.split("&")
  }
  @TypeConverter
  fun mapToString(map: List<Pair<String, String>>): String {
    return Json.encodeToString(map)
  }
  @TypeConverter
  fun stringToMap(string: String): List<Pair<String, String>> {
    return Json.decodeFromString(string)
  }
}