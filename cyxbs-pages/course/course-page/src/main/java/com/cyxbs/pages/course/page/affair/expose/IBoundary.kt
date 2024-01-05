package com.cyxbs.pages.course.page.affair.expose

import androidx.core.view.isGone
import com.cyxbs.pages.course.view.course.ICourseViewGroup
import com.cyxbs.pages.course.view.course.lp.ItemLayoutParams
import kotlin.math.max
import kotlin.math.min

/**
 * 计算能够滑动的上下边界区域
 *
 * @author 985892345
 * 2023/1/30 16:00
 */
interface IBoundary {
  
  /**
   * 得到能到达的上边界，每次有效移动都会回调
   * @param initialRow 手指刚触摸时的行数
   * @param nowRow 当前手指触摸的行数
   * @param initialColumn 手指刚触摸时的列数
   */
  fun getUpperRow(course: ICourseViewGroup, initialRow: Int, nowRow: Int, initialColumn: Int): Int {
    var upperRow = 0
    for ((child, _) in course.container.getItemByViewMap()) {
      if (child.isGone) continue
      val lp = child.layoutParams as ItemLayoutParams
      if (initialColumn in lp.startColumn..lp.endColumn) {
        when {
          initialRow > lp.endRow -> upperRow = max(upperRow, lp.endRow + 1)
          else -> continue // 此时触摸点在一个 item 里面，正常情况下不存在该情况，除非他把其他 item 移开了
        }
      }
    }
    return upperRow
  }
  
  /**
   * 得到能到达的下边界，每次有效移动都会回调
   * @param initialRow 手指刚触摸时的行数
   * @param nowRow 当前手指触摸的行数
   * @param initialColumn 手指刚触摸时的列数
   */
  fun getLowerRow(course: ICourseViewGroup, initialRow: Int, nowRow: Int, initialColumn: Int): Int {
    var lowerRow = course.rowCount - 1
    for ((child, _) in course.container.getItemByViewMap()) {
      if (child.isGone) continue
      val lp = child.layoutParams as ItemLayoutParams
      if (initialColumn in lp.startColumn..lp.endColumn) {
        when {
          initialRow < lp.startRow -> lowerRow = min(lowerRow, lp.startRow - 1)
          else -> continue // 此时触摸点在一个 item 里面，正常情况下不存在该情况，除非他把其他 item 移开了
        }
      }
    }
    return lowerRow
  }
}