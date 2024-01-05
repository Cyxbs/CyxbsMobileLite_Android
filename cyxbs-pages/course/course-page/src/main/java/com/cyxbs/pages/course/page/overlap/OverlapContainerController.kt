package com.cyxbs.pages.course.page.overlap

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import com.cyxbs.pages.course.page.ICoursePage
import com.cyxbs.pages.course.page.controller.CourseController
import com.cyxbs.pages.course.page.item.overlap.IOverlapItem
import com.cyxbs.pages.course.view.item.IItem
import com.cyxbs.pages.course.view.item.IItemInterceptor
import com.cyxbs.pages.course.view.item.OnItemExistListener
import com.cyxbs.pages.course.view.item.forEachColumn
import com.cyxbs.pages.course.view.item.forEachRow
import com.cyxbs.pages.course.view.utils.getOrPut
import com.ndhzs.netlayout.transition.OnChildVisibleListener
import java.util.TreeSet

/**
 * 操控重叠的类
 *
 * 功能如下：
 * - 拦截 [IOverlapItem] 的添加，然后延后自动添加部分符合要求的 item
 * - 观察 item 的移除回调。在被移除时自动取消重叠
 * - 观察 View 的 Visibility，在设置成 GONE 或 VISIBLE 时自动改变重叠状态 (INVISIBLE 不改变)
 *
 * ## 特别注意
 * - 如果你的 item 实现了 [IOverlapItem] 接口，那么在使用 addItem() 添加时会被拦截添加
 * - 然后在下一个 Runnable 中添加 **部分** 之前被拦截的 item (原因请看下方)
 *
 * ## 为什么要拦截使用 addItem() 添加进来的 [IOverlapItem] ?
 * addItem() 后会立马初始化 View 对象，但是并不是所有的 item 都会被显示在课表上，如果全部初始化，
 * 会导致大量 View 对象的产生
 *
 * ## 为什么要在下一个 Runnable 中添加 View ?
 * 根据事件机制，我在上一个 Runnable 中拦截了你添加进来的 item，如果想恢复的话，只能选择在下一个 Runnable 中添加。
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/8 12:19
 */
open class OverlapContainerController : CourseController(), IOverlapContainer {

  override fun compareOverlayItem(row: Int, column: Int, o1: IOverlapItem, o2: IOverlapItem): Int {
    return o1.compareTo(o2)
  }

  final override fun changeOverlap(item: IOverlapItem?, isOverlap: Boolean): Boolean {
    if (item == null) return false
    if (mItemInParentSet.contains(item)) {
      val result = if (isOverlap) setOverlap(item) else deleteOverlap(item)
      if (result) {
        tryPostRefreshOverlapRunnable()
        return true
      }
    }
    return false
  }

  final override fun refreshOverlap(itemsWithoutAnim: List<IOverlapItem>) {
    mItemsWithoutAnim.addAll(itemsWithoutAnim)
    tryPostRefreshOverlapRunnable()
  }

  override fun onCreateView(page: ICoursePage) {
    super.onCreateView(page)
    // 观察子 View 的添加，筛选出 IOverlapItem
    page.course.container.addItemInterceptor(object : IItemInterceptor {
      override fun onAddItem(item: IItem): Boolean {
        return if (item is IOverlapItem) {
          mItemInFreeSet.add(item) // 先暂时保存起来
          setOverlap(item)
          tryPostRefreshOverlapRunnable()
          return true // IOverlapItem 需要延迟添加进去
        } else false
      }

      override fun onRemoveItem(item: IItem) {
        if (item is IOverlapItem) {
          if (mItemInFreeSet.remove(item)) {
            deleteOverlap(item)
            item.overlap.clearOverlap()
            tryPostRefreshOverlapRunnable()
          }
        }
      }
    })
    // 子 View 被移除时需要取消重叠
    page.course.container.addItemExistListener(object : OnItemExistListener {
      override fun onItemRemovedBefore(item: IItem, view: View?) {
        if (item is IOverlapItem) {
          if (mItemInParentSet.remove(item)) {
            deleteOverlap(item)
            item.overlap.clearOverlap()
            tryPostRefreshOverlapRunnable()
          }
        }
      }
    })
    // 设置子 View visibility 改变时的监听
    page.course.addChildVisibleListener(object : OnChildVisibleListener {
      override fun onHideView(parent: ViewGroup, child: View, newVisibility: Int) {
        if (newVisibility == View.INVISIBLE) return // 只监听 GONE 和 VISIBLE
        val item = page.course.container.getItemByView(child)
        if (item is IOverlapItem) {
          changeOverlap(item, false)
        }
      }

      override fun onShowView(parent: ViewGroup, child: View, oldVisibility: Int) {
        if (oldVisibility == View.INVISIBLE) return // 只监听 GONE 和 VISIBLE
        val item = page.course.container.getItemByView(child)
        if (item is IOverlapItem) {
          changeOverlap(item, true)
        }
      }
    })
  }

  override fun onUnBind() {
    super.onUnBind()
    // 回收 item，解决 Fragment 与 View 生命周期不一致问题
    mItemInFreeSet.clear()
    mItemInParentSet.clear()
    mRowColumnMap.clear()
    mItemsWithoutAnim.clear()
    _isPostRefreshOverlapRunnable = false
  }

  private val mItemInFreeSet = hashSetOf<IOverlapItem>()
  private val mItemInParentSet = hashSetOf<IOverlapItem>()
  private val mItemsWithoutAnim = hashSetOf<IOverlapItem>()

  // 判断是否重复发送了 Runnable。你只管直接调用 tryPostRefreshOverlapRunnable() 即可，是否为重复调用由该变量判断
  private var _isPostRefreshOverlapRunnable = false

  // 尝试发送重叠区域改变的 Runnable。这里面会将 mItemInFreeSet 保存的 item 尝试添加进 course
  private fun tryPostRefreshOverlapRunnable(): Boolean {
    if (!_isPostRefreshOverlapRunnable) {
      _isPostRefreshOverlapRunnable = true
      page.course.view.post {
        _isPostRefreshOverlapRunnable = false
        // 先重新询问没有添加进去的 item 是否需要添加进父布局
        val iterator = mItemInFreeSet.iterator()
        while (iterator.hasNext()) {
          val next = iterator.next()
          if (next.overlap.isAddIntoParent()) {
            // 这里 addItem 又会回调 前面的 IItemInterceptor
            page.course.container.addItem(next)
            iterator.remove()
            mItemInParentSet.add(next)
          }
        }
        // 遍历所有添加进去的 item 刷新重叠区域
        mItemInParentSet.forEach {
          it.overlap.refreshOverlap(!mItemsWithoutAnim.contains(it))
        }
        /*
        * 上面的逻辑简单来说就是：
        * 遍历 mItemInFreeSet(被拦截的集合)，如果符合要求，就添加进 course，然后保存在 mItemInParentSet 中，
        * 最后刷新 mItemInParentSet 的 item
        *
        * 可以发现，mItemInParentSet 只有在使用 removeItem() 后才会被删除，达到一定条件时，最后 mItemInFreeSet
        * 会全部存入 mItemInParentSet 中
        * */

        mItemsWithoutAnim.clear()
      }
      return true
    }
    return false
  }

  /**
   * 设置重叠，不一定是在 item 被添加进来时调用
   */
  private fun setOverlap(item: IOverlapItem): Boolean {
    var result = false
    item.lp.forEachRow { row ->
      item.lp.forEachColumn { column ->
        if (getGrid(row, column).setOverlap(item)) {
          result = true
        }
      }
    }
    return result
  }

  /**
   * 删除重叠
   */
  private fun deleteOverlap(item: IOverlapItem): Boolean {
    var result = false
    item.lp.forEachRow { row ->
      item.lp.forEachColumn { column ->
        if (getGrid(row, column).deleteOverlap(item)) {
          result = true
        }
      }
    }
    return result
  }

  private val mRowColumnMap = SparseArray<SparseArray<Grid>>()

  private fun getGrid(row: Int, column: Int): Grid {
    return mRowColumnMap.getOrPut(row) { SparseArray() }
      .getOrPut(column) { Grid(row, column) }
  }

  /**
   * 管理每个表格的工具类
   *
   * 课表是网格布局，每个格子都有一条引用链，串联起了这个格子上的所有 item。
   * 正常情况下只会显示最顶上的 item，如果你将顶上的 item removed 或者 gone，引用链会重新计算并刷新显示
   */
  private inner class Grid(val row: Int, val column: Int) {

    /**
     * TreeSet 该数据结构有个特点就是如果 Compare 比较出来等于 0，则会把之前那个给覆盖掉
     */
    private val sort = TreeSet<IOverlapItem> { o1, o2 ->
      compareOverlayItem(row, column, o1, o2).also {
        if (it == 0 && o1 !== o2) {
          // 如果你想在它们为 0 时删除之前的数据，那么你应该在数据源中删除，而不是在这里删除
          throw RuntimeException("不允许在对象不相等时返回 0 !")
        }
      }
    }

    /**
     * 设置重叠
     */
    fun setOverlap(item: IOverlapItem): Boolean {
      if (!sort.contains(item)) {
        sort.add(item)
        val higher = sort.higher(item)
        higher?.overlap?.onBelowItem(row, column, item)
        item.overlap.onAboveItem(row, column, higher)

        val lower = sort.lower(item)
        item.overlap.onBelowItem(row, column, lower)
        lower?.overlap?.onAboveItem(row, column, item)
        return true
      }
      return false
    }

    /**
     * 删除重叠
     *
     * 注意：这个 remove 必须要满足 Compare 比较等于 0 时才能被 remove 掉
     */
    fun deleteOverlap(item: IOverlapItem): Boolean {
      if (sort.contains(item)) {
        val higher = sort.higher(item)
        val lower = sort.lower(item)
        higher?.overlap?.onBelowItem(row, column, lower)
        lower?.overlap?.onAboveItem(row, column, higher)
        item.overlap.onBelowItem(row, column, null)
        item.overlap.onAboveItem(row, column, null)
        sort.remove(item)
        return true
      }
      return false
    }
  }
}