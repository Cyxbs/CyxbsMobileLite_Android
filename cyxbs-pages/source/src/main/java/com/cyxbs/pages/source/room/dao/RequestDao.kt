package com.cyxbs.pages.source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cyxbs.pages.source.room.entity.RequestCacheEntity
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.cyxbs.pages.source.room.entity.RequestItemContentsEntity
import com.cyxbs.pages.source.room.entity.RequestItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 14:13
 */
@Dao
abstract class RequestDao {

  @Insert
  abstract fun insert(requestItemEntity: RequestItemEntity)

  @Update
  abstract fun change(requestItemEntity: RequestItemEntity)

  @Transaction
  open fun insert(requestContentEntity: RequestContentEntity) {
    val item = findItemByName(requestContentEntity.name)
      ?: throw IllegalArgumentException("不存在 name = ${requestContentEntity.name} 的 RequestContentEntity")
    val id = insertInternal(requestContentEntity)
    change(item.copy(sort = item.sort + id)) // 维护排序
    requestContentEntity.id = id
  }

  @Update
  abstract fun change(requestContentEntity: RequestContentEntity)

  @Insert
  protected abstract fun insertInternal(requestContentEntity: RequestContentEntity): Long

  @Query("SELECT * FROM request_item")
  abstract fun getItems(): List<RequestItemEntity>

  @Delete
  abstract fun removeItem(item: RequestItemEntity)

  @Delete
  abstract fun removeContent(content: RequestContentEntity)

  @Transaction
  @Query("SELECT * FROM request_item WHERE name = :name")
  abstract fun findByName(name: String): RequestItemContentsEntity?

  @Query("SELECT * FROM request_item WHERE name = :name")
  abstract fun findItemByName(name: String): RequestItemEntity?

  @Query("SELECT * FROM request_content WHERE name = :name")
  abstract fun findContentsByName(name: String): List<RequestContentEntity>

  @Query("SELECT * FROM request_cache WHERE name = :name AND `values` = :values")
  abstract fun findCache(name: String, values: List<String>): RequestCacheEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun changeOrInsertCache(cache: RequestCacheEntity)

  @Transaction
  @Query("SELECT * FROM request_item")
  abstract fun observeAll(): Flow<List<RequestItemContentsEntity>>

  @Query("SELECT * FROM request_item WHERE name = :name")
  abstract fun observeItem(name: String): Flow<RequestItemEntity?>

  @Query("SELECT * FROM request_item")
  abstract fun observeItems(): Flow<List<RequestItemEntity>>

  @Transaction
  @Query("SELECT * FROM request_item WHERE name = :name")
  abstract fun observeItemContents(name: String): Flow<RequestItemContentsEntity?>
}