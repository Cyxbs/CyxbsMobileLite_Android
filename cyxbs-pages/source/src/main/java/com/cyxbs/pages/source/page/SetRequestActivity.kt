package com.cyxbs.pages.source.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.view.view.JToolbar
import com.cyxbs.components.view.view.ScaleScrollEditText
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.page.viewmodel.SetRequestViewModel
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.toast

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
      parameters: List<Pair<String, String>>
    ) {
      context.startActivity(
        Intent(context, SetRequestActivity::class.java)
          .putExtra(SetRequestActivity::mRequestContent.name, content)
          .putExtra(SetRequestActivity::mParameters.name, ArrayList(parameters))
      )
    }
  }

  private val mRequestContent by intent<RequestContentEntity>()
  private val mParameters by intent<List<Pair<String, String>>>()

  private val mViewModel: SetRequestViewModel by viewModels()

  private val mEtTitle: EditText by R.id.source_et_title.view()
  private val mEtUrl: EditText by R.id.source_et_url.view()
  private val mScaleScrollEditTextJs: ScaleScrollEditText by R.id.source_sset_js.view()
  private val mBtnOk: Button by R.id.source_btn_ok.view()
  private val mBtnTest: Button by R.id.source_btn_test.view()
  private val mBtnRestore: Button by R.id.source_btn_restore.view()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initToolbar()
    initEtTitle()
    initEtUrl()
    initEtJs()
    initBtn()
  }

  private fun initToolbar() {
    val toolbar = findViewById<JToolbar>(com.cyxbs.components.view.R.id.toolbar)
    toolbar.init(this, mRequestContent.name)
    if (mRequestContent.url != null || mRequestContent.js != null) {
      toolbar.setRightIcon(R.drawable.source_ic_baseline_delete_outline_24).apply {
        setOnSingleClickListener {
          mViewModel.removeContent(mRequestContent)
          finish()
        }
      }
    }
  }

  private fun initEtTitle() {
    mEtTitle.setText(mRequestContent.title)
  }

  private fun initEtUrl() {
    mEtUrl.setText(mRequestContent.url)
  }

  private fun initEtJs() {
    mScaleScrollEditTextJs.hint = """
      请输入 js
      
      规则：
        1. 只填写 url，会自动获取页面文本，可用于 GET 请求
        2. 只填写 js，将直接执行，可用 js 发起 POST 请求
        2. 填写 url 和 js，则在页面加载完后执行 js
      
      与端上交互规则:
        // 调用 success() 返回结果，只允许调用一次
        androidBridge.success('...'); 
        // 调用 error() 返回异常，只允许调用一次
        androidBridge.error('...');
        // 调用 toast() 触发应用 toast，但只在测试时才显示
        androidBridge.toast('...');
        
      端上传递请求参数规则:
        端上可以传递参数到 url 和 js 上
        引用规则: 
          以 {TEXT} 的方式进行引用, 在请求会进行字符串替换
        例子:
          比如端上设置参数为: stu_num, 取值为: abc
          则对于如下 url: https://test/{stu_num}
          会被替换为: https://test/abc
          js 同样如此，但请注意这只是简单的替换字符串
        
      该面板支持双指放大缩小
    """.trimIndent()
    mScaleScrollEditTextJs.text = mRequestContent.js
  }

  private fun initBtn() {
    mBtnOk.setOnSingleClickListener {
      createNewContent()?.let {
        mViewModel.changeOrInsertContent(it)
        finish()
      }
    }
    mBtnTest.setOnSingleClickListener {
      createNewContent()?.let {
        TestRequestActivity.start(this, it.url, it.js, mParameters)
      }
    }
    mBtnRestore.setOnSingleClickListener {
      mEtTitle.setText(mRequestContent.title)
      mEtUrl.setText(mRequestContent.url)
      mScaleScrollEditTextJs.text = mRequestContent.js
    }
  }

  private fun createNewContent(): RequestContentEntity? {
    val url = mEtUrl.text.toString().ifBlank { null }
    val js = mScaleScrollEditTextJs.text.toString().ifBlank { null }
    if (url != null || js != null) {
      return mRequestContent.copy(
        title = mEtTitle.text.toString(),
        url = url,
        js = js
      )
    } else {
      toast("url 和 js 必须设置一个")
    }
    return null
  }
}