package com.cyxbs.functions.account

import androidx.core.content.edit
import com.cyxbs.components.config.sp.defaultSp
import com.cyxbs.functions.api.account.IAccountService
import com.g985892345.android.extensions.android.processLifecycleScope
import com.g985892345.provider.api.annotation.ImplProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 13:06
 */
@ImplProvider
object AccountServiceImpl : IAccountService {

  private val stuNumState = MutableStateFlow<String?>(null)

  override fun observeStuNumState(): StateFlow<String?> {
    return stuNumState.asStateFlow()
  }

  override fun observeStuNumEvent(): SharedFlow<String?> {
    return stuNumState.asSharedFlow()
  }

  override fun setStuNum(stuNum: String?) {
    processLifecycleScope.launch {
      stuNumState.emit(stuNum)
    }
    defaultSp.edit {
      putString("学号", stuNum)
    }
  }

  override fun getStuNum(): String? {
    return stuNumState.value
  }

  init {
    setStuNum(defaultSp.getString("学号", null))
  }
}