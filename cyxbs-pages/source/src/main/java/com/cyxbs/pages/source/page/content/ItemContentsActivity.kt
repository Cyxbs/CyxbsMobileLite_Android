package com.cyxbs.pages.source.page.content

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.components.base.ui.CyxbsBaseActivity
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.view.view.JToolbar
import com.cyxbs.functions.api.network.internal.IRequestService
import com.cyxbs.pages.api.source.IDataSourceService
import com.cyxbs.pages.source.R
import com.cyxbs.pages.source.data.RequestContentItemData
import com.cyxbs.pages.source.page.request.SetRequestActivity
import com.cyxbs.pages.source.page.adapter.ItemContentsAdapter
import com.cyxbs.pages.source.page.viewmodel.RequestContentsViewModel
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.g985892345.android.base.ui.page.viewModelBy
import com.g985892345.android.extensions.android.gone
import com.g985892345.android.extensions.android.mainHandler
import com.g985892345.android.extensions.android.postDelay
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.size.dp2px
import com.g985892345.android.extensions.android.size.dp2pxF
import com.g985892345.android.extensions.android.toast
import com.g985892345.android.extensions.android.visible
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:08
 */
class ItemContentsActivity : CyxbsBaseActivity(R.layout.source_activity_item_contents) {

  companion object {
    fun start(context: Context, name: String) {
      context.startActivity(
        Intent(context, ItemContentsActivity::class.java)
          .putExtra(ItemContentsActivity::mName.name, name)
      )
    }
  }

  private val mName: String by intent()

  private val mViewModel: RequestContentsViewModel by viewModelBy {
    RequestContentsViewModel(mName)
  }

  private val mEtInterval: EditText by R.id.source_et_interval.view()
  private val mRecyclerview : RecyclerView by R.id.source_rv_contents.view()
  private val mFloatFl: FrameLayout by R.id.source_fl_float.view()
  private val mFloatBtn: FloatingActionButton by R.id.source_btn_float.view()
  private val mViewEmptyHolder: View by R.id.source_view_empty_holder.view()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initToolbar()
    initEtInterval()
    initRecyclerView()
    initFloatBtn()
  }

  private fun initToolbar() {
    findViewById<JToolbar>(com.cyxbs.components.view.R.id.toolbar)
      .init(this, mName)
  }

  private fun initEtInterval() {
    mViewModel.contentsData.observe {
      mEtInterval.setText(it.item.interval.toString())
    }
    mEtInterval.setOnEditorActionListener { v, action, _ ->
      if (action == EditorInfo.IME_ACTION_DONE) {
        mViewModel.changeInterval(v.text.toString().toFloat())
        mEtInterval.clearFocus()
        getSystemService(InputMethodManager::class.java)
          .hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        toast("设置成功")
        return@setOnEditorActionListener true
      }
      false
    }
  }

  private fun initRecyclerView() {
    mRecyclerview.layoutManager = LinearLayoutManager(this)
    val adapter = ItemContentsAdapter()
    mRecyclerview.adapter = adapter
    mViewModel.contentsData.observe { data ->
      adapter.submitList(data.contents.map {
        RequestContentItemData(data.item, it)
      })
      if (data.contents.isNotEmpty()) {
        mViewEmptyHolder.gone()
      } else {
        mViewEmptyHolder.visible()
      }
    }
    // 长按上下移动，设置顺序
    ItemTouchHelper(object : ItemTouchHelper.Callback() {
      override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
      ): Int {
        return makeFlag(ACTION_STATE_IDLE, UP or DOWN) or
            makeFlag(ACTION_STATE_DRAG, UP or DOWN or LEFT or RIGHT)
      }

      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
      ): Boolean {
        val contents = mViewModel.contentsData.value?.contents ?: return false
        val content1 = contents[viewHolder.layoutPosition]
        val content2 = contents[target.layoutPosition]
        return mViewModel.swapContentEntity(content1, content2)
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
      }
    }).apply { attachToRecyclerView(mRecyclerview) }
  }

  private fun initFloatBtn() {
    mFloatBtn.setOnSingleClickListener {
      runFloatAnim()
    }
  }

  private var mIsOpenFloat = false

  private fun runFloatAnim() {
    if (mIsOpenFloat) {
      floatCloseAnim()
    } else {
      floatOpenAnim()
    }
    mIsOpenFloat = !mIsOpenFloat
  }

  private val mDataSourceServices = ServiceManager.getAllImpl(IDataSourceService::class)
    .mapValues { it.value.get() }

  private val mFloatChildViews by lazy {
    mDataSourceServices
      .map { (key, service) ->
        FloatingActionButton(this).apply {
          setImageDrawable(service.drawable)
          backgroundTintList = AppCompatResources.getColorStateList(
            context, R.color.source_float_btn_item)
          alpha = 0F
          elevation = 2.dp2pxF
          size = FloatingActionButton.SIZE_MINI
          setOnSingleClickListener {
            val itemData = mViewModel.contentsData.value ?: return@setOnSingleClickListener
            SetRequestActivity.start(
              it.context, RequestContentEntity(
                name = itemData.item.name,
                title = "${itemData.item.name}${itemData.contents.size}",
                serviceKey = key,
                data = "",
                error = null,
                response = null,
                requestTimestamp = null,
                responseTimestamp = null,
              ), itemData.item.parameters, itemData.item.output
            )
            mainHandler.postDelay(500) {
              runFloatAnim() // 延后执行 close 动画，不与 activity 跳转一起
            }
          }
        }
      }.onEach {
        mFloatFl.addView(
          it,
          FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
          ).apply {
            bottomMargin = mFloatBtn.marginBottom + 16.dp2px
          }
        )
      }
  }

  private fun floatOpenAnim() {
    mFloatChildViews.onEachIndexed { index, button ->
      button.rotation = -90F
      button.animate()
        .translationY(-(index + 1) * 50.dp2pxF)
        .alpha(1F)
        .rotationBy(90F)
        .setInterpolator(OvershootInterpolator(1F))
        .setDuration(400)
        .withEndAction {
          button.isClickable = true
        }.start()
    }
    mFloatBtn.animate()
      .rotationBy(45F)
      .start()
  }

  private fun floatCloseAnim() {
    mFloatChildViews.onEach {
      it.animate()
        .translationY(0F)
        .alpha(0F)
        .rotationBy(-90F)
        .setInterpolator(OvershootInterpolator(1F))
        .setDuration(600)
        .start()
      it.isClickable = false
    }
    mFloatBtn.animate()
      .rotationBy(-45F)
      .start()
  }
}