package com.cyxbs.pages.exam.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cyxbs.components.config.config.SchoolCalendar
import com.cyxbs.components.utils.Num2CN
import com.cyxbs.pages.exam.R
import com.cyxbs.pages.exam.bean.ExamBean
import java.util.Calendar

/**
 * .
 *
 * @author 985892345
 * 2023/5/11 11:35
 */
class ExamListAdapter : ListAdapter<ExamBean, ExamListAdapter.ListVH>(
  object : DiffUtil.ItemCallback<ExamBean>() {
    override fun areItemsTheSame(oldItem: ExamBean, newItem: ExamBean): Boolean {
      return oldItem.areItemsTheSame(newItem)
    }
  
    override fun areContentsTheSame(oldItem: ExamBean, newItem: ExamBean): Boolean {
      return oldItem.areContentsTheSame(newItem)
    }
  }
) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListVH {
    return ListVH(LayoutInflater.from(parent.context).inflate(R.layout.exam_rv_item_list, parent, false))
  }
  
  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ListVH, position: Int) {
    val data = (getItem(position) as ExamBean)
    holder.mTvWeekDay.text = getWeekDay(data.week, data.weekNum)
    holder.mTvRemain.text = getRemain(data.week, data.weekNum, data.startTime, data.endTime)
    holder.mTvName.text = data.name
    holder.mTvType.text = data.type
    holder.mTvMonthDay.text = getMonthDay(data.week, data.weekNum)
    holder.mTvTime.text = "${data.startTime}-${data.endTime}"
    holder.mTvRoom.text = data.room
    holder.mTvSeat.text = data.seat + "号"
  }
  
  private fun getWeekDay(week: Int, weekNum: Int): String {
    return if (week < 10) {
      "第" + Num2CN.transform(week.toLong()) + "周周${WEEK_NUM[weekNum - 1]}"
    } else {
      Num2CN.transform(week.toLong()) + "周周${WEEK_NUM[weekNum - 1]}"
    }
  }
  
  private fun getRemain(week: Int, weekNum: Int, startTime: String, endTime: String): String {
    val weekOfTerm = SchoolCalendar.getWeekOfTerm() ?: return ""
    val calendar = Calendar.getInstance()
    val a = week * 7 + weekNum
    val b = weekOfTerm * 7 + (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
    val sHour = startTime.substringBefore(":").toInt()
    val sMinute = startTime.substringAfter(":").toInt()
    val sTime = sHour * 60 + sMinute
    val eHour = endTime.substringBefore(":").toInt()
    val eMinute = endTime.substringAfter(":").toInt()
    val eTime = eHour * 60 + eMinute
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val nTime = hour * 60 + minute
    return if (a < b) {
      "考试已结束"
    } else if (a == b) {
      if (nTime < sTime) {
        if (sTime - nTime > 30) {
          "今天考试"
        } else {
          "该准备去考场了"
        }
      } else if (nTime < eTime) {
        "考试进行中"
      } else {
        "考试已结束"
      }
    } else {
      "还剩${a - b}天考试"
    }
  }
  
  private fun getMonthDay(week: Int, weekNum: Int): String {
    val calendar = SchoolCalendar.getFirstMonDayOfTerm() ?: return "未知"
    calendar.add(Calendar.DATE, (week - 1) * 7 + weekNum - 1)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return "${month}月${day}号"
  }
  
  companion object {
    private val WEEK_NUM = arrayOf("一", "二", "三", "四", "五", "六", "日")
  }
  
  inner class ListVH(itemView: View) : ViewHolder(itemView) {
    val mTvWeekDay: TextView = itemView.findViewById(R.id.exam_tv_rv_item_week_day)
    val mTvRemain: TextView = itemView.findViewById(R.id.exam_tv_rv_item_remain)
    val mTvName: TextView = itemView.findViewById(R.id.exam_tv_rv_item_exam_name)
    val mTvType: TextView = itemView.findViewById(R.id.exam_rv_tv_exam_type)
    val mTvMonthDay: TextView = itemView.findViewById(R.id.exam_tv_rv_item_exam_month_day)
    val mTvTime: TextView = itemView.findViewById(R.id.exam_tv_rv_item_exam_time)
    val mTvRoom: TextView = itemView.findViewById(R.id.exam_tv_rv_item_exam_room)
    val mTvSeat: TextView = itemView.findViewById(R.id.exam_tv_rv_item_exam_seat)
    
    init {
      itemView.findViewById<View>(R.id.exam_cl_rv_item_item).setOnLongClickListener {
//        ChooseDialog.Builder(
//          itemView.context,
//          ChooseDialog.Data(
//            content = "需要添加到课表中吗?",
//            width = 280,
//            height = 160,
//          )
//        ).setPositiveClick {
//          val bean = (getItem(bindingAdapterPosition) as ExamBean)
//          addIntoCourse(bean)
//          dismiss()
//        }.setNegativeClick {
//          dismiss()
//        }.show()
        true
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