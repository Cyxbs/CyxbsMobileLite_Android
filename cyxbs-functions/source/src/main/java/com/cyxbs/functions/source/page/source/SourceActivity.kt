package com.cyxbs.functions.source.page.source

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.config.route.SOURCE_ENTRY
import com.cyxbs.components.view.view.JToolbar
import com.cyxbs.functions.source.R
import com.cyxbs.functions.source.page.adapter.SourceAdapter
import com.cyxbs.functions.source.page.viewmodel.SourceViewModel
import com.g985892345.provider.annotation.KClassProvider

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 14:44
 */
@KClassProvider(name = SOURCE_ENTRY)
class SourceActivity : CyxbsBaseActivity(R.layout.source_activity_source) {

  private val mViewModel: SourceViewModel by viewModels()

  private val mRecyclerView: RecyclerView by R.id.source_rv_source.view()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initToolbar()
    initRecyclerView()
  }

  private fun initToolbar() {
    findViewById<JToolbar>(com.cyxbs.components.view.R.id.toolbar)
      .init(this, "数据源", titleOnLeft = false)
  }

  private fun initRecyclerView() {
    mRecyclerView.layoutManager = LinearLayoutManager(this)
    val adapter = SourceAdapter()
    mRecyclerView.adapter = adapter
    mViewModel.requestItemContentsData.observe {
      adapter.submitList(it)
    }
  }
}