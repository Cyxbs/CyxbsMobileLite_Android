package com.cyxbs.functions.source.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cyxbs.functions.source.room.dao.RequestDao
import com.cyxbs.functions.source.room.entity.RequestCacheEntity
import com.cyxbs.functions.source.room.entity.RequestContentEntity
import com.cyxbs.functions.source.room.entity.RequestItemEntity
import com.g985892345.android.utils.context.appContext

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 14:12
 */
@Database(
  entities = [
    RequestItemEntity::class,
    RequestContentEntity::class,
    RequestCacheEntity::class
  ],
  version = 1
)
abstract class SourceDataBase : RoomDatabase() {

  abstract val requestDao: RequestDao

  companion object {
    val INSTANCE by lazy {
      Room.databaseBuilder(
        appContext,
        SourceDataBase::class.java,
        "source_db"
      ).fallbackToDestructiveMigration().build()
    }
  }
}