package com.cyxbs.pages.exam.model

import com.cyxbs.components.router.impl
import com.cyxbs.components.view.crash.CrashDialog
import com.cyxbs.functions.api.account.IAccountService
import com.cyxbs.pages.exam.bean.ExamBean
import com.cyxbs.pages.exam.room.ExamDataBase
import com.cyxbs.pages.exam.room.ExamEntity
import com.cyxbs.pages.exam.service.ExamDataServiceImpl
import com.g985892345.android.extensions.android.lazyUnlock
import com.g985892345.android.extensions.android.processLifecycleScope
import com.g985892345.android.utils.context.topActivity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
  fun observeSelfExam(): Flow<List<ExamEntity>> {
    return IAccountService::class.impl
      .observeStuNumState()
      .flatMapLatest {
        if (it == null) emptyFlow() else observeExam(it)
      }
  }

  /**
   * 观察考试数据
   */
  fun observeExam(stuNum: String): Flow<List<ExamEntity>> {
    // 如果学号为空就发送空数据给下游
    if (stuNum.isBlank()) return emptyFlow()
    return mExamDao.observeExam(stuNum)
      .onStart {
        processLifecycleScope.launch {
          runCatching {
            refreshLesson(stuNum, false)
          }.onFailure {
            CrashDialog.Builder(topActivity!!, it).show()
          }
        }
      }.distinctUntilChanged()
  }
  
  /**
   * 联网刷新考试数据
   *
   * 需要内网
   */
  suspend fun refreshLesson(
    stuNum: String,
    isForce: Boolean,
  ): List<ExamEntity> {
    if (stuNum.isBlank()) throw IllegalArgumentException("学号不能为空！")
    return ExamDataServiceImpl.request(isForce, stuNum)
      .let {
        Json.decodeFromString<List<ExamBean>>(it)
      }.let { list ->
        list.map { it.toExamEntity(stuNum) }
      }.also {
        withContext(Dispatchers.IO) {
          mExamDao.resetData(stuNum, it)
        }
      }
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