package com.cyxbs.pages.course.vp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.course.vp.holder.CourseViewHolder

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/14 22:39
 */
open class CourseAdapter : RecyclerView.Adapter<CourseViewHolder>() {

  init {
    super.setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
    return LayoutInflater.from(parent.context)
      .inflate(com.cyxbs.pages.course.page.R.layout.course_page_layout_page, parent, false)
      .let { CourseViewHolder(it) }
  }

  override fun getItemCount(): Int {
    return 21
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
    holder.bind()
  }

  override fun onViewRecycled(holder: CourseViewHolder) {
    super.onViewRecycled(holder)
    holder.unbind()
  }
}