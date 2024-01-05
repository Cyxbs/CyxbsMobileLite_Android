package com.cyxbs.pages.course

import androidx.viewpager2.widget.ViewPager2
import com.cyxbs.components.base.ui.CyxbsBaseFragment


/**
 * .
 *
 * @author 985892345
 * @date 2023/10/16 00:08
 */
class TestFragment : CyxbsBaseFragment(R.layout.course_layout_test) {
  init {

    ViewPager2(requireContext()).setCurrentItem()
  }
}