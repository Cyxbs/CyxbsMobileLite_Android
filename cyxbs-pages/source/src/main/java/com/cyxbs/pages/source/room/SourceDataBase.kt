package com.cyxbs.pages.source.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cyxbs.pages.source.room.dao.RequestDao
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.cyxbs.pages.source.room.entity.RequestItemEntity
import com.g985892345.android.utils.context.appContext

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 14:12
 */
@Database(entities = [RequestItemEntity::class, RequestContentEntity::class], version = 1)
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