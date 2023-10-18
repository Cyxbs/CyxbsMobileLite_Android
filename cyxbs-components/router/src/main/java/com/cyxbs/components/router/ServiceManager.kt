package com.cyxbs.components.router

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.g985892345.android.extensions.android.appContext
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
   * IAccountService::class.impl
   *   .isLogin()
   * ```
   */
  operator fun <T : Any> invoke(serviceClass: KClass<T>): T {
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
    return getImplOrThrow(null, servicePath, singleton = false)
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
   * @param singleton
   * - false: 返回 @NewImplProvider 实现类；
   * - true: 返回 @SingleImplProvider 实现类；
   * - null: 优先返回 @SingleImplProvider，若无，则返回 @NewImplProvider
   */
  fun <T : Any> getImplOrNull(
    serviceClass: KClass<T>?,
    name: String = "",
    singleton: Boolean? = null
  ): T? =  KtProviderManager.getImplOrNull(serviceClass, name, singleton)

  /**
   * 注释查看: [getImplOrNull]
   */
  fun <T : Any> getImplOrThrow(
    serviceClass: KClass<T>?,
    name: String = "",
    singleton: Boolean? = null
  ): T =  KtProviderManager.getImplOrThrow(serviceClass, name, singleton)

  /**
   * 返回 name 对应的 KClass
   */
  fun <T : Any> getKClassOrNull(name: String): KClass<out T>? =
    KtProviderManager.getKClassOrNull(name)

  /**
   * 返回 name 对应的 KClass
   */
  fun <T : Any> getKClassOrThrow(name: String): KClass<out T> =
    KtProviderManager.getKClassOrThrow(name)
}