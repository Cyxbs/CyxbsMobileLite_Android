package com.cyxbs.pages.course.page.item.overlap

import com.cyxbs.pages.course.view.item.IItem

/**
 * 支持重叠的 item
 *
 * - 控制重叠功能的类请看 [OverlapImpl]
 * - 实现类请看 [AbstractOverlapSingleDayItem]，目前该类只能用于单列的情况
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/25 17:30
 */
interface IOverlapItem : IItem, Comparable<IOverlapItem> {
  
  val overlap: ICourseOverlap
  
  override fun compareTo(other: IOverlapItem): Int
}