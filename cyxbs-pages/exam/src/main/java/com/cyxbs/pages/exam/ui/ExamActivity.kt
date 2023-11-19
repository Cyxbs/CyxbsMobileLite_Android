package com.cyxbs.pages.exam.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.config.route.EXAM_ENTRY
import com.cyxbs.components.view.crash.CrashDialog
import com.cyxbs.components.view.view.JToolbar
import com.cyxbs.pages.exam.R
import com.cyxbs.pages.exam.adapter.ExamHeadAdapter
import com.cyxbs.pages.exam.adapter.ExamListAdapter
import com.cyxbs.pages.exam.ui.viewmodel.ExamViewModel
import com.g985892345.android.extensions.android.gone
import com.g985892345.android.extensions.android.toast
import com.g985892345.android.extensions.android.visible
import com.g985892345.provider.annotation.KClassProvider

@KClassProvider(name = EXAM_ENTRY)
class ExamActivity : CyxbsBaseActivity() {
  
  private val mViewModel by viewModels<ExamViewModel>()
  
  private val mRvExam by R.id.exam_rv.view<RecyclerView>()
  private val mRefresh by R.id.exam_refresh.view<SwipeRefreshLayout>()
  private val mLLNoData by R.id.exam_ll_no_data.view<View>()
  private val mTvNoData by R.id.exam_tv_no_data.view<TextView>()
  
  private val mListAdapter = ExamListAdapter()
  private val mHeadAdapter = ExamHeadAdapter {
    mListAdapter.currentList
  }
  
  private val mAdapter = ConcatAdapter(mHeadAdapter, mListAdapter)
  
  private var mNoDataIndex = 0
  private val mNoDataTexts = arrayOf(
    "还是莫得数据",
    "别刷了，就是莫得数据",
    "说了多少遍了，就是莫得数据",
    "☹️☹️☹️"
  )
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.exam_activity_exam)
    initToolbar()
    initRv()
    initRefresh()
    initObserve()
  }
  
  private fun initToolbar() {
    findViewById<JToolbar>(com.cyxbs.components.view.R.id.toolbar).init(this, "我的考试")
  }
  
  private fun initRv() {
    mRvExam.adapter = mAdapter
    mRvExam.layoutManager = LinearLayoutManager(this)
    mListAdapter.submitList(emptyList())
  }
  
  private fun initRefresh() {
    mRefresh.setOnRefreshListener {
      if (mNoDataIndex < mNoDataTexts.size) {
        mViewModel.refreshExam()
      } else {
        mRefresh.isRefreshing = false
      }
    }
  }
  
  private fun initObserve() {
    mViewModel.examBean.observe {
      mListAdapter.submitList(it)
      if (it.isEmpty()) {
        mLLNoData.visible()
      } else {
        mLLNoData.gone()
      }
    }

    mViewModel.refreshEvent.collectLaunch {
      mRefresh.isRefreshing = false
      if (it == null) {
        toast("刷新成功")
        if (mNoDataIndex < mNoDataTexts.size) {
          mTvNoData.text = mNoDataTexts[mNoDataIndex++]
        }
      } else {
        CrashDialog.Builder(this, it)
          .show()
      }
    }
  }
}