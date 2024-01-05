package com.cyxbs.functions.account

import androidx.core.content.edit
import com.cyxbs.components.config.sp.defaultSp
import com.cyxbs.components.utils.Nullable
import com.cyxbs.functions.api.account.IAccountService
import com.g985892345.provider.api.annotation.ImplProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 13:06
 */
@ImplProvider
object AccountServiceImpl : IAccountService {

  private val stuNumState = BehaviorSubject.create<Nullable<String>>()
  private val stuNumEvent = PublishSubject.create<Nullable<String>>()

  override fun observeStuNumState(): Observable<Nullable<String>> {
    return stuNumState.distinctUntilChanged()
  }

  override fun observeStuNumEvent(): Observable<Nullable<String>> {
    return stuNumEvent.distinctUntilChanged()
  }

  override fun setStuNum(stuNum: String?) {
    val value = Nullable(stuNum)
    stuNumState.onNext(value)
    stuNumEvent.onNext(value)
    defaultSp.edit {
      putString("学号", stuNum)
    }
  }

  override fun getStuNum(): String? {
    return stuNumState.value?.value
  }

  init {
    setStuNum(defaultSp.getString("学号", null))
  }
}