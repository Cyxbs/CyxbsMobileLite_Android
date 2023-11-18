package com.cyxbs.pages.source.page.request

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.view.view.JToolbar
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.room.SourceDataBase
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.g985892345.android.extensions.android.color
import com.g985892345.android.extensions.android.launch
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.utils.adapter.vp.FragmentVpAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/19 19:35
 */
class SetRequestActivity : CyxbsBaseActivity(R.layout.source_activity_set_request) {

  companion object {
    fun start(
      context: Context,
      content: RequestContentEntity,
      parameters: List<Pair<String, String>>,
      output: String,
    ) {
      context.startActivity(
        Intent(context, SetRequestActivity::class.java)
          .putExtra(SetRequestActivity::mRequestContent.name, content)
          .putExtra(SetRequestActivity::mParameters.name, ArrayList(parameters))
          .putExtra(SetRequestActivity::mOutput.name, output)
      )
    }
  }

  private val mRequestContent by intent<RequestContentEntity>()
  private val mParameters by intent<List<Pair<String, String>>>()
  private val mOutput by intent<String>()

  private val mTabLayout: TabLayout by R.id.source_tl_set_request.view()
  private val mViewPager: ViewPager2 by R.id.source_vp_set_request.view()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initToolbar()
    initViewPager()
  }

  private fun initToolbar() {
    val toolbar = findViewById<JToolbar>(com.cyxbs.components.view.R.id.toolbar)
    toolbar.init(
      this, "设置请求",
      withSplitLine = false,
      titleOnLeft = false,
      background = com.cyxbs.components.config.R.color.config_white_black.color
    )
    if (mRequestContent.data.isNotEmpty()) {
      toolbar.setRightIcon(R.drawable.source_ic_baseline_delete_outline_24).apply {
        setOnSingleClickListener {
          launch(Dispatchers.IO) {
            SourceDataBase.INSTANCE
              .requestDao
              .removeContent(mRequestContent)
            // 确保数据成功删除才允许 finish
            withContext(Dispatchers.Main) {
              finish()
            }
          }
        }
      }
    }
  }

  private fun initViewPager() {
    mViewPager.adapter = FragmentVpAdapter(this)
      .add { InputOutputFragment.newInstance(mParameters, mOutput) }
      .add { CodeFragment.newInstance(mRequestContent, mParameters) }
    TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
      when (position) {
        0 -> tab.text = "请求格式"
        1 -> tab.text = "请求脚本"
      }
    }.attach()
    mViewPager.currentItem = 1
  }
}