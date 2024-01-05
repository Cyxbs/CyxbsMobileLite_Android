package com.cyxbs.pages.course.vp.pool

import com.cyxbs.pages.course.view.item.IItem

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/14 22:55
 */
interface IItemPool<Data, Item : IItem> {

  /**
   * 获取空闲的 Item
   */
  fun get(data: Data): Item

  /**
   * 回收空闲的 Item
   */
  fun put(item: Item, data: Data)
}