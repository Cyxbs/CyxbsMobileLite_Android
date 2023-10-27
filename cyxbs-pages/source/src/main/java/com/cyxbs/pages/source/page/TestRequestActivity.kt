package com.cyxbs.pages.source.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.view.crash.CrashDialog
import com.cyxbs.components.view.view.ScaleScrollTextView
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.resquest.RequestManager
import com.cyxbs.pages.source.resquest.RequestUnit
import com.g985892345.android.extensions.android.launch
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/26 22:10
 */
class TestRequestActivity : CyxbsBaseActivity(R.layout.source_activity_test) {

  companion object {
    fun start(context: Context, url: String?, js: String?, parameters: List<Pair<String, String>>) {
      context.startActivity(
        Intent(context, TestRequestActivity::class.java)
          .putExtra(TestRequestActivity::mUrl.name, url)
          .putExtra(TestRequestActivity::mJs.name, js)
          .putExtra(TestRequestActivity::mParameters.name, ArrayList(parameters))
      )
    }

    private val TestRequestUnit by lazy { RequestUnit(true) }
  }

  private val mUrl by intentNullable<String>()
  private val mJs by intentNullable<String>()
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

  private fun initTvResult() {
    mTvResult.hint = "点击该面板进行请求，之后将显示结果"
    mTvResult.setOnSingleClickListener {
      if (mRequestJob != null) {
        toast("正在请求中")
      } else {
        if (mEtParameters.all { !it.text.isNullOrEmpty() }) {
          mRequestJob = launch {
            val parameters = mParameters.map { it.first }
            val values = mEtParameters.map { it.text.toString() }
            val url = RequestManager.replaceValue(mUrl, parameters, values)
            val js = RequestManager.replaceValue(mJs, parameters, values)
            try {
              toast("开始测试...")
              val result = TestRequestUnit.load(url, js)
              mTvResult.text = result
            } catch (e: Exception) {
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