package com.cyxbs.components.router

/**
 *
 *
 * @author 985892345
 * @date 2023/10/19 00:13
 */
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
inline val <reified T : IService> T.impl: T
  get() = ServiceManager(T::class)