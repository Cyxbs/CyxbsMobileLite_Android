package com.cyxbs.pages.course.page.affair.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import com.cyxbs.pages.course.page.R
import com.cyxbs.pages.course.page.affair.expose.ITouchAffairItem
import com.cyxbs.pages.course.page.item.single.SingleDayLayoutParams
import com.cyxbs.pages.course.page.item.touch.helper.expand.utils.DoubleSideExpandableHelper
import com.cyxbs.pages.course.page.item.touch.helper.expand.utils.ISingleSideExpandable
import com.cyxbs.pages.course.page.item.view.ItemView
import com.cyxbs.pages.course.view.course.ICourseViewGroup
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.dimen
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.extensions.android.setOnSingleClickListener

/**
 * 这是长按空白区域生成的带有加号的那个灰色 View。用于点击一下打开添加事务界面
 *
 * @author 985892345
 * @date 2022/9/19 14:55
 */
open class TouchAffairView(context: Context) : ItemView(context), ITouchAffairItem {
  
  protected open val mSideExpandable: ISingleSideExpandable by lazyUnlock {
    newSideExpandable()
  }
  
  protected open fun newSideExpandable(): ISingleSideExpandable {
    return DoubleSideExpandableHelper()
  }
  
  override fun onMoveStart(course: ICourseViewGroup, row: Int, column: Int) {
    lp.startRow = row
    lp.endRow = row
    lp.startColumn = column
    lp.endColumn = column
    course.container.addItem(this)
    mSideExpandable.onMoveStart(course, this, this)
  }
  
  override fun onSingleMove(course: ICourseViewGroup, row: Int, y: Int) {
    mSideExpandable.onSingleMove(course, this, this, row, y)
  }
  
  override fun onMoveEnd(course: ICourseViewGroup) {
    mSideExpandable.onMoveEnd(course, this, this, true)
  }
  
  override fun isInShow(): Boolean {
    return parent != null
  }
  
  override fun cancelShow() {
    // 离场动画
    startAnimation(AlphaAnimation(1F, 0F).apply { duration = 200 })
    (parent as ViewGroup).removeView(this)
  }
  
  override fun cloneLp(): SingleDayLayoutParams {
    return SingleDayLayoutParams(lp).apply {
      changeAll(lp)
    }
  }
  
  override fun setOnClickListener(l: () -> Unit) {
    setOnSingleClickListener { l.invoke() }
  }
  
  protected val mImageView = ImageView(context).apply {
    scaleType = ImageView.ScaleType.CENTER_INSIDE
    // 设置 ImageView 的前景图片
    setImageResource(R.drawable.course_page_ic_touch_affair)
  }
  
  init {
    super.setCardBackgroundColor(R.color.course_page_affair_bg.color)
    super.addView(
      mImageView,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)
    )
  }
  
  override fun initializeView(context: Context): View {
    return this
  }
  
  override val lp = SingleDayLayoutParams(-1, -1, 0).apply {
    leftMargin = R.dimen.course_page_item_margin.dimen.toInt() // 课表中 item 的边距值
    rightMargin = leftMargin
    topMargin = leftMargin
    bottomMargin = leftMargin
  }
}