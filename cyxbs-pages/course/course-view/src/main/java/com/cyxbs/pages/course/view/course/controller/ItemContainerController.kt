package com.cyxbs.pages.course.view.course.controller

import android.view.View
import android.view.ViewGroup
import com.cyxbs.pages.course.view.course.CourseViewGroup
import com.cyxbs.pages.course.view.item.IItem
import com.cyxbs.pages.course.view.item.IItemContainer
import com.cyxbs.pages.course.view.item.IItemInterceptor
import com.cyxbs.pages.course.view.item.OnItemExistListener
import com.cyxbs.pages.course.view.utils.forEachReversed
import com.ndhzs.netlayout.child.OnChildExistListener
import java.util.Collections

/**
 * [IItemContainer] 的实现类
 *
 * 功能如下：
 * - 实现 [IItem] 的添加
 * - 关联 View 的移除回调，在 View 被移除时同时移除 Item
 * - 提供监听 Item 添加、移除的方法
 * - 提供查找 Item 的方法
 *
 * @author 985892345
 * @date 2023/11/30 22:52
 */
class ItemContainerController(
  val course: CourseViewGroup
) : IItemContainer {

  private val mOnItemExistListeners = ArrayList<OnItemExistListener>(2)
  private val mItemInterceptors = ArrayList<IItemInterceptor>(2)
  private val mItemByView = HashMap<View, IItem>()
  private val mViewByItem = HashMap<IItem, View?>()
  private val mInterceptorByItem = HashMap<IItem, IItemInterceptor>()

  init {
    // 使用其他方式删除子 View 时也需要同步删除 item
    course.addChildExistListener(
      object : OnChildExistListener {
        override fun onChildViewRemoved(parent: ViewGroup, child: View) {
          val item = mItemByView[child] ?: return
          removeItem(item)
        }
      }
    )
  }

  override fun addItemExistListener(l: OnItemExistListener) {
    mOnItemExistListeners.add(l)
  }

  override fun removeItemExistListener(l: OnItemExistListener) {
    mOnItemExistListeners.remove(l)
  }

  override fun addItemInterceptor(interceptor: IItemInterceptor) {
    mItemInterceptors.add(interceptor)
  }

  override fun addItem(item: IItem) {
    val interceptor = mInterceptorByItem[item]
    if (interceptor == null) {
      check(!mViewByItem.contains(item)) { "该 Item 已经被添加了" }
      mOnItemExistListeners.forEachReversed { it.onItemAddedBefore(item) }
      val view = addItemInternal(item, null)
      mOnItemExistListeners.forEachReversed { it.onItemAddedAfter(item, view) }
    } else {
      mInterceptorByItem.remove(item)
      addItemInternal(item, interceptor)
    }
  }

  private fun addItemInternal(item: IItem, oldInterceptor: IItemInterceptor?): View? {
    var isIntercept = false
    for (it in mItemInterceptors) {
      if (it !== oldInterceptor && it.onAddItem(item)) {
        isIntercept = true
        mInterceptorByItem[item] = it
        break
      }
    }
    var view: View? = null
    if (!isIntercept) {
      // 此时才能添加进课表
      view = item.initializeView(course.context)
      mItemByView[view] = item
      if (item.lp !== item.lp) {
        throw IllegalStateException("不允许每次调用 item.lp 生成不同的对象")
      }
      course.addNetChild(view, item.lp)
    }
    mViewByItem[item] = view
    return view
  }

  override fun removeItem(item: IItem) {
    val interceptor = mInterceptorByItem.remove(item)
    if (interceptor != null) {
      mOnItemExistListeners.forEachReversed { it.onItemRemovedBefore(item, null) }
      interceptor.onRemoveItem(item)
      mOnItemExistListeners.forEachReversed { it.onItemRemovedAfter(item, null) }
    } else {
      val view = mViewByItem[item]
      if (view != null) {
        mOnItemExistListeners.forEachReversed { it.onItemRemovedBefore(item, view) }
        mItemByView.remove(view)
        mViewByItem.remove(item)
        course.removeView(view) // 这一步需要在最后
        mOnItemExistListeners.forEachReversed { it.onItemRemovedAfter(item, view) }
      }
    }
  }

  override fun getItemByView(view: View?): IItem? {
    return mItemByView[view]
  }

  override fun getViewByItem(item: IItem?): View? {
    return mViewByItem[item]
  }

  override fun getItemByViewMap(): Map<View, IItem> {
    return Collections.unmodifiableMap(mItemByView)
  }

  override fun getViewByItemMap(): Map<IItem, View?> {
    return Collections.unmodifiableMap(mViewByItem)
  }

  override fun findItemUnderByXY(x: Int, y: Int): IItem? {
    val view = course.findViewUnderByXY(x, y)
    return getItemByView(view)
  }

  override fun findPairUnderByXY(x: Int, y: Int): Pair<IItem, View>? {
    val view = course.findViewUnderByXY(x, y)
    val item = getItemByView(view)
    if (item != null && view != null) {
      return Pair(item, view)
    }
    return null
  }

  override fun findPairUnderByFilter(filter: IItem.(View) -> Boolean): Pair<IItem, View>? {
    for (i in course.childCount - 1 downTo 0) {
      val child = course.getChildAt(i)
      val item = mItemByView[child]
      if (item != null) {
        if (filter.invoke(item, child)) {
          return Pair(item, child)
        }
      }
    }
    return null
  }
}