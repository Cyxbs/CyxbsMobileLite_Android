package com.cyxbs.pages.course.view.course

import android.view.View
import android.view.ViewGroup
import com.cyxbs.pages.course.view.course.lp.ItemLayoutParams
import com.cyxbs.pages.course.view.item.IItemContainer
import com.cyxbs.pages.course.view.touch.IMultiTouch
import com.ndhzs.netlayout.child.ChildExistListenerContainer
import com.ndhzs.netlayout.draw.ItemDecorationContainer
import com.ndhzs.netlayout.orientation.IColumn
import com.ndhzs.netlayout.orientation.IRow
import com.ndhzs.netlayout.save.SaveStateListenerContainer
import com.ndhzs.netlayout.transition.ChildVisibleListenerContainer
import com.ndhzs.netlayout.view.NetLayout

/**
 * 课表控件的总接口
 *
 * 但请注意：这只是一个 ViewGroup，缺失很多实际课表的逻辑，如果命名为 CourseLayout，总感觉不合适，
 * 所以改名为 ICourseViewGroup，而很多业务逻辑都移到了 Fragment 层
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 12:58
 */
interface ICourseViewGroup : IColumn, IRow,
  ItemDecorationContainer, // 绘制监听
  SaveStateListenerContainer, // 状态保存
  ChildExistListenerContainer, // 监听子 View 添加和删除
  ChildVisibleListenerContainer // 监听子 View 的 visible
{

  val view: ViewGroup
    get() = this as ViewGroup

  /**
   * debug 属性，开启后会绘制表格
   */
  var debug: Boolean

  /**
   * 多指触摸
   */
  val touch: IMultiTouch

  /**
   * 滚轴
   */
  val scroll: ICourseScrollControl

  /**
   * item 容器
   */
  val container: IItemContainer

  /**
   * 设置子 View 添加进来时的顺序
   */
  fun setCompareLayoutParams(compare: Comparator<ItemLayoutParams>)
  
  /**
   * 倒序查找子 View (该方法由 [NetLayout] 实现)
   *
   * 倒序的原因是因为一般排在后面的显示在最上面
   *
   * @see findViewUnderByRowColumn
   */
  fun findViewUnderByXY(x: Int, y: Int): View?
  
  /**
   * 根据行和列倒序查找子 View (该方法由 [NetLayout] 实现)
   * @see findViewUnderByXY
   */
  fun findViewUnderByRowColumn(row: Int, column: Int): View?
}