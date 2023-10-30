package com.cyxbs.pages.exam.service

import com.cyxbs.functions.api.network.AbstractDataService
import com.g985892345.provider.annotation.SingleImplProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/28 22:12
 */
@SingleImplProvider
object ExamDataServiceImpl : AbstractDataService(
  listOf(
    "stuNum" to "学号"
  ),
  """
    // 返回一个 json 格式的集合
    [
      {
        week: Int, // 周次
        weekNum: Int, // 星期数，从 1 开始
        startTime: String, // 开始时间: 16:10
        endTime: String, // 结束时间: 18:10
        beginLesson: Int, // 课的起始节数
        period: Int, // 课的长度
        name: String, // 课程名称
        classNumber: String, // 课程编号
        type: String, // 考试类型
        room: String, // 考试地点
        seat: String, // 考试座位
      }，
      {
        // 跟上面一样
      }
    ]
  """.trimIndent()
) {

}