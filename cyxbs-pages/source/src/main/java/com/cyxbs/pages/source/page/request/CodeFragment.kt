package com.cyxbs.pages.source.page.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import com.cyxbs.components.base.ui.CyxbsBaseFragment
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.view.view.ScaleScrollEditText
import com.cyxbs.pages.api.source.IDataSourceService
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.page.test.TestRequestActivity
import com.cyxbs.pages.source.room.SourceDataBase
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.g985892345.android.extensions.android.launch
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/29 21:19
 */
class CodeFragment : CyxbsBaseFragment(R.layout.source_fragment_code) {

  companion object {
    fun newInstance(
      content: RequestContentEntity,
      parameters: List<Pair<String, String>>,
    ): CodeFragment = CodeFragment().apply {
      arguments = bundleOf(
        this::mRequestContent.name to content,
        this::mParameters.name to parameters,
      )
    }
  }

  private val mRequestContent by arguments<RequestContentEntity>()
  private val mParameters by arguments<List<Pair<String, String>>>()

  private val mDataSourceService by lazyUnlock {
    ServiceManager.getImplOrThrow(IDataSourceService::class, mRequestContent.serviceKey)
  }

  private val mLlHeader: LinearLayout by R.id.source_ll_header.view()
  private lateinit var mEtTitle: EditText
  private val mScaleScrollEditText: ScaleScrollEditText by R.id.source_sset_js.view()
  private val mBtnOk: Button by R.id.source_btn_ok.view()
  private val mBtnTest: Button by R.id.source_btn_test.view()

  private lateinit var mHeader: List<View>

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initHeader()
    initBtn()
  }

  private fun initHeader() {
    val title = LayoutInflater.from(requireContext())
      .inflate(com.cyxbs.api.source.R.layout.source_item_code_header, mLlHeader, false)
    mLlHeader.addView(title)
    title.findViewById<TextView>(com.cyxbs.api.source.R.id.source_tv_header).apply {
      text = "设置标题: "
    }
    mEtTitle = title.findViewById(com.cyxbs.api.source.R.id.source_et_header)
    mEtTitle.setText(mRequestContent.title)
    mHeader = mDataSourceService.config(
      mRequestContent.data.ifBlank { null },
      mScaleScrollEditText,
      mLlHeader
    ).onEach {
      mLlHeader.addView(it)
    }
  }

  private fun initBtn() {
    mBtnOk.setOnSingleClickListener {
      createNewContent()?.let { content ->
        launch(Dispatchers.IO) {
          SourceDataBase.INSTANCE
            .requestDao.apply {
              if (content.id == 0L) insert(content) else change(content)
            }
          // 在 activity 中插入数据，确保数据插入完才允许 finish
          withContext(Dispatchers.Main) {
            requireActivity().finish()
          }
        }
      }
    }
    mBtnTest.setOnSingleClickListener {
      createNewContent()?.let {
        TestRequestActivity.start(requireContext(), it, mParameters)
      }
    }
  }

  private fun createNewContent(): RequestContentEntity? {
    return mDataSourceService.createData(mScaleScrollEditText, mHeader)?.let {
      mRequestContent.copy(
        title = mEtTitle.text.toString(),
        data = it
      )
    }
  }
}