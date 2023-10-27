package com.cyxbs.pages.source.page

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.view.view.JToolbar
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.page.adapter.SourceAdapter
import com.cyxbs.pages.source.page.viewmodel.SourceViewModel

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 14:44
 */
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
      .init(this, "数据源")
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