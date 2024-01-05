package com.cyxbs.pages.course.view.item

/**
 * 拦截 View 的添加
 *
 * item 虽然会被添加进容器，但是可以通过该监听延迟添加 View
 *
 * @author 985892345
 * @date 2023/11/30 23:18
 */
interface IItemInterceptor {

  /**
   * 拦截 item 的添加
   *
   * 返回 true 后将不再继续遍历后面的 [IItemInterceptor]
   *
   * 拦截后如果需要继续添加，请再次调用 [IItemContainer.addItem]，
   * 此时不会再次回调自身拦截器的 [onAddItem]，但是仍然可以被其他拦截器拦截
   *
   * @return true：进行拦截；false：不进行拦截
   */
  fun onAddItem(item: IItem): Boolean

  /**
   * 移除 item 时的回调
   */
  fun onRemoveItem(item: IItem)
}