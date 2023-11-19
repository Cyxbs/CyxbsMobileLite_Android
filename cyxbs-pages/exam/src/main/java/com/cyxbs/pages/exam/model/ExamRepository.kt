package com.cyxbs.pages.exam.model

import com.cyxbs.components.router.impl
import com.cyxbs.components.view.crash.CrashDialog
import com.cyxbs.functions.api.account.IAccountService
import com.cyxbs.pages.exam.bean.ExamBean
import com.cyxbs.pages.exam.room.ExamDataBase
import com.cyxbs.pages.exam.room.ExamEntity
import com.cyxbs.pages.exam.service.ExamDataServiceImpl
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.utils.context.topActivity
import com.g985892345.jvm.rxjava.unsafeSubscribeBy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.serialization.json.Json

/**
 * .
 *
 * @author 985892345
 * 2023/4/8 19:09
 */
object ExamRepository {
  
  private val mExamDao by lazyUnlock { ExamDataBase.INSTANCE.getExamDao() }

  /**
   * 观察自己的考试数据
   * - 支持换账号登录后返回新登录人的数据
   * - 使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * - 没登录时发送 emptyList()
   */
  fun observeSelfExam(): Observable<List<ExamEntity>> {
    return IAccountService::class.impl
      .observeStuNumState()
      .observeOn(Schedulers.io())
      .switchMap { value ->
        // 使用 switchMap 可以停止之前学号的订阅
        value.nullUnless(Observable.just(emptyList())) {
          observeExam(it)
        }
      }
  }

  /**
   * 观察考试数据
   */
  fun observeExam(stuNum: String): Observable<List<ExamEntity>> {
    // 如果学号为空就发送空数据给下游
    if (stuNum.isBlank()) return Observable.just(emptyList())
    return mExamDao.observeExam(stuNum)
      .doOnSubscribe {
        refreshLesson(stuNum, false)
          .doOnError {
            CrashDialog.Builder(topActivity!!, it)
              .show()
          }.unsafeSubscribeBy()
      }.distinctUntilChanged()
      .subscribeOn(Schedulers.io())
  }
  
  /**
   * 联网刷新考试数据
   *
   * 需要内网
   */
  fun refreshLesson(
    stuNum: String,
    isForce: Boolean,
  ): Single<List<ExamEntity>> {
    if (stuNum.isBlank()) return Single.error(IllegalArgumentException("学号不能为空！"))
    return ExamDataServiceImpl.request(isForce, stuNum)
      .map {
        Json.decodeFromString<List<ExamBean>>(it)
      }.map { list ->
        list.map { it.toExamEntity(stuNum) }
      }.doOnError {
      }.doOnSuccess {
        mExamDao.resetData(stuNum, it)
      }.subscribeOn(Schedulers.io())
  }

  /**
   * 得到考试数据
   */
  fun getExam(stuNum: String): Single<List<ExamEntity>> {
    return Single.create {
      it.onSuccess(mExamDao.getExam(stuNum))
    }.subscribeOn(Schedulers.io())
  }
}