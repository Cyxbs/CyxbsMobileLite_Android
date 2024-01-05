package com.cyxbs.pages.course.view.scroll

import android.content.Context
import android.util.AttributeSet
import com.cyxbs.pages.course.view.scroll.base.ScrollControlImpl

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 0:39
 */
class CourseScrollView(
  context: Context,
  attrs: AttributeSet?,
) : ScrollControlImpl(context, attrs) {

  init {
    /**
     * 取消 Android11 及以下的滑到边缘的虚影，Android12 及以上滑到边缘的拉伸效果
     *
     * Android12 后滑到边缘时的拉伸效果，会导致嵌套滑动失效，所以需要取消该效果
     */
    overScrollMode = OVER_SCROLL_NEVER
  }
}