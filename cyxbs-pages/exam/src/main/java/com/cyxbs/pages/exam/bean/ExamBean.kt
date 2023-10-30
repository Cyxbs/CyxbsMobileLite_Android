package com.cyxbs.pages.exam.bean

import com.cyxbs.pages.exam.room.ExamEntity
import kotlinx.serialization.Serializable

/**
 * .
 *
 * @author 985892345
 * 2023/4/8 15:13
 */
@Serializable
data class ExamBean(
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
  fun areItemsTheSame(other: ExamBean): Boolean {
    return name == other.name
      && classNumber == other.classNumber
      && type == other.type
  }
  
  fun areContentsTheSame(other: ExamBean): Boolean {
    return this == other
  }
  
  fun toExamEntity(stuNum: String): ExamEntity {
    return ExamEntity(
      stuNum,
      week,
      weekNum,
      startTime,
      endTime,
      beginLesson,
      period,
      name,
      classNumber,
      type,
      room,
      seat
    )
  }
}

fun List<ExamEntity>.toExamBean(): List<ExamBean> {
  return map {
    ExamBean(
      it.week,
      it.weekNum,
      it.startTime,
      it.endTime,
      it.beginLesson,
      it.period,
      it.name,
      it.classNumber,
      it.type,
      it.room,
      it.seat
    )
  }
}