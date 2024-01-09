package com.cyxbs.pages.exam.room

import androidx.room.*
import com.g985892345.android.utils.context.appContext
import kotlinx.coroutines.flow.Flow

/**
 * .
 *
 * @author 985892345
 * 2023/4/8 19:01
 */
@Database(entities = [ExamEntity::class], version = 1)
abstract class ExamDataBase : RoomDatabase() {
  
  abstract fun getExamDao(): ExamDao
  
  companion object {
    val INSTANCE by lazy {
      Room.databaseBuilder(
        appContext,
        ExamDataBase::class.java,
        "exam_db"
      ).fallbackToDestructiveMigration().build()
    }
  }
}

@Entity(tableName = "exam")
data class ExamEntity(
  val stuNum: String,
  val week: Int, // 周次
  val weekNum: Int, // 星期数，从 1 开始
  val startTime: String, // 开始时间: 16:10
  val endTime: String, // 结束时间: 18:10
  val beginLesson: Int, // 课的起始节数
  val period: Int, // 课的长度
  val name: String, // 课程名称
  val classNumber: String, // 课程编号
  val type: String, // 考试类型
  val room: String, // 考试地点
  val seat: String, // 考试座位
) {
  @PrimaryKey(autoGenerate = true)
  var id = 0
}

@Dao
abstract class ExamDao {
  
  @Query("SELECT * FROM exam WHERE stuNum = :stuNum")
  abstract fun observeExam(stuNum: String): Flow<List<ExamEntity>>
  
  @Query("SELECT * FROM exam WHERE stuNum = :stuNum")
  abstract fun getExam(stuNum: String): List<ExamEntity>
  
  @Query("DELETE FROM exam WHERE stuNum = :stuNum")
  protected abstract fun deleteExam(stuNum: String)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  protected abstract fun insertExam(exam: List<ExamEntity>)
  
  @Transaction
  open fun resetData(stuNum: String, exam: List<ExamEntity>) {
    deleteExam(stuNum)
    insertExam(exam)
  }
}