package com.cyxbs.functions.api.account

import com.cyxbs.components.router.IService
import com.cyxbs.components.utils.Nullable
import io.reactivex.rxjava3.core.Observable

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 12:36
 */
interface IAccountService : IService {

  /**
   * 观察学号的改变（状态）
   *
   * 有数据倒灌的 Observable，每次订阅会发送之前的最新值
   *
   * ## 注意
   * ### 1、Activity 和 Fragment 中使用一般需要切换线程
   * ```
   * observeOn(AndroidSchedulers.mainThread())
   * ```
   *
   * ### 2、生命周期问题
   * 模块中 BaseActivity 已自带了 safeSubscribeBy() 方法用于关联生命周期
   *
   * ## 其他问题
   * ### 1、为什么使用 Rxjava，不使用 Flow ?
   * Flow 目前还有很多 api 处于测试阶段，不如 Rxjava 稳定
   *
   * ### 2、单一流装换为多流
   * 如果你想对于不同学号返回给下游不同的 Observable，**需要使用 [Observable.switchMap]**，因为它可以自动停止上一个发送的 Observable
   * ```
   * 写法如下：
   * observeStuNumState()
   *   .observeOn(Schedulers.io()) // 注意：你需要使用 observeOn 才能切换线程，subscribeOn 无法切换发送源的线程
   *   .switchMap { value ->
   *     // switchMap 可以在上游发送新的数据时自动关闭上一次数据生成的 Observable
   *     value.nullUnless(Observable.never()) {
   *       if (stuNum.isEmpty()) Observable.never()
   *       else LessonDataBase.INSTANCE.getStuLessonDao() // 数据库
   *         .observeAllLesson(stuNum) // 观察数据库的数据变动，这是 Room 的响应式编程
   *         .distinctUntilChanged() // 必加，因为 Room 每次修改都会回调，所以需要加这个去重
   *         .doOnSubscribe {
   *           getLesson(stuNum, isNeedOldList).safeSubscribeBy() // 在开始订阅时请求一次云端数据
   *         }.map { StuResult(stuNum, it) }
   *         .subscribeOn(Schedulers.io())
   *     }
   *   }
   * ```
   *
   * ### 3、为什么使用 Value 包裹 ?
   * 因为 Rxjava 不允许数据为空值，所以使用 Value 包裹了一层
   *
   * - 更多注意事项请看 [observeStuNumEvent]
   */
  fun observeStuNumState(): Observable<Nullable<String>>

  /**
   * 观察学号的改变（事件）
   *
   * 没有数据倒灌的 Observable，即每次订阅不会发送之前的最新值
   *
   * ## 更多注意事项请看 [observeStuNumState]
   */
  fun observeStuNumEvent(): Observable<Nullable<String>>

  /**
   * 设置学号
   */
  fun setStuNum(stuNum: String?)

  /**
   * 得到学号
   */
  fun getStuNum(): String?
}