package com.cyxbs.pages.exam.single.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.cyxbs.components.config.config.SchoolCalendar
import com.cyxbs.components.config.route.SOURCE_ENTRY
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.components.router.impl
import com.cyxbs.components.view.dialog.BaseChooseDialog
import com.cyxbs.functions.api.account.IAccountService
import com.cyxbs.pages.exam.R
import com.g985892345.android.extensions.android.toast

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 17:47
 */
class AccountDialog private constructor(
  context: Context
) : BaseChooseDialog<AccountDialog, AccountDialog.Data>(context) {

  class Data(
    override val positiveButtonText: String = "设置请求",
    override val type: DialogType = DialogType.ONE_BUT
  ): BaseChooseDialog.Data

  class Builder(context: Context) : BaseChooseDialog.Builder<AccountDialog, Data>(context, Data()) {
    override fun buildInternal(): AccountDialog {
      setPositiveClick {
        val nowWeek = mEtWeek.text.toString().toIntOrNull()
        if (mEtStuNum.text.isNotBlank() && nowWeek != null) {
          IAccountService::class.impl
            .setStuNum(mEtStuNum.text.toString())
          SchoolCalendar.updateFirstCalendar(nowWeek)
          ServiceManager.activity(SOURCE_ENTRY)
          dismiss()
        } else {
          toast("请输入完整")
        }
      }
      return AccountDialog(context)
    }
  }

  private val mEtWeek: EditText by R.id.exam_et_dialog_week.view()
  private val mEtStuNum: EditText by R.id.exam_et_dialog_stu_num.view()

  override fun createContentView(parent: ViewGroup): View {
    return LayoutInflater.from(parent.context)
      .inflate(R.layout.exam_dialog_setting, parent, false)
  }

  override fun initContentView(view: View) {
    mEtWeek.setText(SchoolCalendar.getWeekOfTerm()?.toString())
    mEtStuNum.setText(IAccountService::class.impl.getStuNum())
  }
}