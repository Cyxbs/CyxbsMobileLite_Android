package com.cyxbs.functions.source.page.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.view.crash.CrashDialog
import com.cyxbs.components.view.view.ScaleScrollTextView
import com.cyxbs.functions.source.R
import com.cyxbs.functions.source.room.entity.RequestContentEntity
import com.cyxbs.pages.api.source.IDataSourceService
import com.g985892345.android.extensions.android.launch
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.timeout
import kotlin.time.Duration.Companion.seconds

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/26 22:10
 */
class TestRequestActivity : CyxbsBaseActivity(R.layout.source_activity_test) {

  companion object {
    fun start(
      context: Context,
      content: RequestContentEntity,
      parameters: List<Pair<String, String>>
    ) {
      context.startActivity(
        Intent(context, TestRequestActivity::class.java)
          .putExtra(TestRequestActivity::mRequestContentEntity.name, content)
          .putExtra(TestRequestActivity::mParameters.name, ArrayList(parameters))
      )
    }
  }

  private val mRequestContentEntity by intent<RequestContentEntity>()
  private val mParameters by intent<List<Pair<String, String>>>()
  private val mEtParameters = mutableListOf<EditText>()

  private val mLlParameter: LinearLayout by R.id.source_ll_test_parameter.view()
  private val mTvResult: ScaleScrollTextView by R.id.source_sstv_test_result.view()

  private var mRequestJob: Job? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initParameter()
    initTvResult()
  }

  private fun initParameter() {
    mParameters.forEach {
      val layout = LayoutInflater.from(this)
        .inflate(R.layout.source_item_parameter, mLlParameter, false)
      val tv: TextView = layout.findViewById(R.id.source_tv_parameter_name)
      val et: EditText = layout.findViewById(R.id.source_et_parameter)
      tv.text = it.first
      et.hint = it.second
      mEtParameters.add(et)
      mLlParameter.addView(layout)
    }
  }

  @OptIn(FlowPreview::class)
  private fun initTvResult() {
    mTvResult.post {
      mTvResult.maxTextWidth = mTvResult.width
    }
    mTvResult.hint = "点击该面板进行请求，之后将显示结果"
    mTvResult.setOnSingleClickListener {
      if (mRequestJob != null) {
        toast("正在请求中")
      } else {
        if (mEtParameters.all { !it.text.isNullOrEmpty() }) {
          mRequestJob = launch {
            val service = ServiceManager
              .getImplOrThrow(IDataSourceService::class, mRequestContentEntity.serviceKey)
            try {
              toast("开始测试...")
              val result = flow {
                emit(service.request(
                  mRequestContentEntity.data,
                  mParameters.mapIndexed { index, pair ->
                    pair.first to mEtParameters[index].text.toString()
                  }.associate { it }))
              }.timeout(1.seconds)
                .single()
              mTvResult.text = result
            } catch (e: Throwable) {
              if (e is CancellationException) throw e
              mTvResult.text = null
              CrashDialog.Builder(this@TestRequestActivity, e)
                .show()
            } finally {
              mRequestJob = null
            }
          }
        } else {
          toast("参数未填写完")
        }
      }
    }
  }
}