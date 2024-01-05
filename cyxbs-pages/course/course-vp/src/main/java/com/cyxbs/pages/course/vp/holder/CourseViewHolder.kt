package com.cyxbs.pages.course.vp.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.course.page.CoursePage

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/14 22:37
 */
class CourseViewHolder(
  itemView: View,
) : RecyclerView.ViewHolder(itemView) {

  private val mCoursePage = CoursePage()

  init {
    mCoursePage.create(itemView)
  }

  fun bind() {
    mCoursePage.bind()
  }

  fun unbind() {
    mCoursePage.unBind()
    recycleItem()
  }

  /**
   * 并没有合适的被摧毁的回调，正常情况下 adapter 内的 holder 会一直处于复用状态
   */
  fun destroy() {
    mCoursePage.destroy()
    recycleItem()
  }

  private fun recycleItem() {

  }
}