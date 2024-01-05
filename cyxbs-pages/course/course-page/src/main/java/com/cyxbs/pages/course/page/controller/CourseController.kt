package com.cyxbs.pages.course.page.controller

import android.view.View
import androidx.annotation.CallSuper
import com.cyxbs.pages.course.page.ICoursePage
import com.g985892345.android.utils.view.bind.BindView

/**
 * .
 *
 * @author 985892345
 * @date 2023/12/3 14:45
 */
abstract class CourseController : ICourseController {

  private var _page: ICoursePage? = null
  protected val page: ICoursePage
    get() = _page!!

  protected fun <T: View> Int.view(): BindView<T> = BindView(this, page::root)

  private var mIsDestroy = false

  @CallSuper
  override fun onCreateView(page: ICoursePage) {
    check(!mIsDestroy) { "已经经过 onDestroyView，不能再次触发 onCreateView" }
    _page = page
  }

  @CallSuper
  override fun onBind() {
  }

  @CallSuper
  override fun onUnBind() {
  }

  @CallSuper
  override fun onDestroyView() {
    _page = null
    mIsDestroy = true
  }
}