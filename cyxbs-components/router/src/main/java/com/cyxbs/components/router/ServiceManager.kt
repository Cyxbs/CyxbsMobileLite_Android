package com.cyxbs.components.router

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.g985892345.android.utils.context.appContext
import com.g985892345.provider.api.init.wrapper.ImplProviderWrapper
import com.g985892345.provider.api.init.wrapper.KClassProviderWrapper
import com.g985892345.provider.manager.KtProviderManager
import kotlin.reflect.KClass

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/15 16:28
 */
object ServiceManager {

  /**
   * 写法：
   * ```
   * ServiceManger(IAccountService::class)
   *   .isLogin()
   * ```
   * 还有更简单的写法：
   * ```
   * IAccountService::class.impl // 但需要 IAccountService 实现 IService 接口
   *   .isLogin()
   * ```
   */
  operator fun <T : Any> invoke(serviceClass: KClass<out T>): T {
    return KtProviderManager.getImplOrThrow(serviceClass)
  }

  /**
   * 写法：
   * ```
   * ServiceManger<IAccountService>(ACCOUNT_SERVICE)
   *   .isLogin()
   * ```
   */
  operator fun <T : Any> invoke(servicePath: String): T {
    return getImplOrThrow(null, servicePath)
  }

  fun fragment(servicePath: String): Fragment {
    return getImplOrThrow(null, servicePath)
  }

  fun activity(servicePath: String) {
    val activityClass = KtProviderManager.getKClassOrThrow<Activity>(servicePath)
    appContext.startActivity(
      Intent(appContext, activityClass.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 因为使用 appContext，所以需要加
    )
  }

  /**
   * 返回设置了对应 clazz 和 name 的实现类
   */
  fun <T : Any> getImplOrNull(
    serviceClass: KClass<out T>?,
    name: String = "",
  ): T? =  KtProviderManager.getImplOrNull(serviceClass, name)

  /**
   * 注释查看: [getImplOrNull]
   */
  fun <T : Any> getImplOrThrow(
    serviceClass: KClass<out T>?,
    name: String = "",
  ): T =  KtProviderManager.getImplOrThrow(serviceClass, name)

  /**
   * 返回 [clazz] 和 [name] 对应的 KClass
   */
  fun <T : Any> getKClassOrNull(
    clazz: KClass<out T>?,
    name: String = ""
  ): KClass<out T>? = KtProviderManager.getKClassOrNull(clazz, name)

  /**
   * 返回 [clazz] 和 [name] 对应的 KClass
   */
  fun <T : Any> getKClassOrThrow(
    clazz: KClass<out T>?,
    name: String = ""
  ): KClass<out T> = KtProviderManager.getKClassOrThrow(clazz, name)

  /**
   * 获取 @ImplProvider 中 clazz 参数为 [clazz] 的所有实现类
   */
  fun <T : Any> getAllImpl(
    clazz: KClass<out T>?
  ): Map<String, ImplProviderWrapper<T>> = KtProviderManager.getAllImpl(clazz)

  /**
   * 获取 @KClassProvider 中 clazz 参数为 [clazz] 的所有实现类
   */
  fun <T : Any> getAllKClass(
    clazz: KClass<out T>?
  ): Map<String, KClassProviderWrapper<T>> = KtProviderManager.getAllKClass(clazz)
}


interface IService

/**
 * 便捷写法
 * ```
 * interface INetworkService : IService
 *
 * // 之后可以这样写
 * IAccountService::class.impl
 *   .getStuNum()
 * ```
 */
inline val <reified T : IService> KClass<T>.impl: T
  get() = ServiceManager(T::class)
