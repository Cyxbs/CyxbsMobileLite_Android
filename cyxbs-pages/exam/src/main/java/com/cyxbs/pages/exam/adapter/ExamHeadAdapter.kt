package com.cyxbs.pages.exam.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cyxbs.pages.exam.R
import com.cyxbs.pages.exam.bean.ExamBean
import com.cyxbs.pages.exam.service.ExamDataServiceImpl
import com.g985892345.android.extensions.android.setOnSingleClickListener
import com.g985892345.android.extensions.android.toast
import com.g985892345.android.extensions.android.toastLong
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

/**
 * .
 *
 * @author 985892345
 * 2023/5/11 11:29
 */
class ExamHeadAdapter(
  val getExamBeanList: () -> List<ExamBean>
) : Adapter<ExamHeadAdapter.HeadVH>() {

  private var mDiffDay = 0L

  private var mUpdateJob: Job? = null

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    mUpdateJob = ExamDataServiceImpl.observeUpdate()
      .onEach {
        val lastUpdateTimestamp = ExamDataServiceImpl.getLastResponseTimestamp() ?: 0L
        val diffDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastUpdateTimestamp)
        if (diffDay >= 5 && lastUpdateTimestamp != 0L) {
          toastLong("已超过 5 天未更新，不能保证数据的正确性 !")
        }
        mDiffDay = diffDay
        notifyItemChanged(0, "")
      }.launchIn(recyclerView.findViewTreeLifecycleOwner()!!.lifecycleScope)
  }

  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    mUpdateJob?.cancel()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadVH {
    return HeadVH(
      LayoutInflater.from(parent.context).inflate(R.layout.exam_rv_item_head, parent, false)
    )
  }

  override fun getItemCount(): Int {
    return 1
  }

  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: HeadVH, position: Int) {
    if (mDiffDay == 0L) {
      holder.mTvHint.text = ""
    } else {
      holder.mTvHint.text = "(${mDiffDay}天前更新)"
    }
  }

  inner class HeadVH(itemView: View) : ViewHolder(itemView) {
    val mTvHint: TextView = itemView.findViewById(R.id.exam_tv_rv_item_hint)

    init {
      itemView.findViewById<View>(R.id.exam_tv_rv_item_add_into_course).setOnSingleClickListener {
        toast("还未实现")
//        ChooseDialog.Builder(
//          itemView.context,
//          ChooseDialog.Data(
//            content = "需要全部添加到课表中吗?\n如果想单个添加，请长按对应考试即可",
//            width = 280,
//            height = 180,
//          )
//        ).setPositiveClick {
//          getExamBeanList().forEach {
//            addIntoCourse(it)
//          }
//          dismiss()
//        }.setNegativeClick {
//          dismiss()
//        }.show()
      }
    }

    private fun addIntoCourse(bean: ExamBean) {
//      IAffairService::class.impl.addAffair(
//        0,
//        "[考试]\n${bean.name}",
//        bean.room + "\n" + bean.seat + "号",
//        listOf(bean.week),
//        listOf(
//          IAffairService.Duration(
//            0L,
//            0L,
//            bean.weekNum - 1,
//            bean.beginLesson,
//            bean.period,
//          )
//        )
//      ).unsafeSubscribeBy()
    }
  }
}