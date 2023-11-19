package com.cyxbs.components.utils

import java.io.Serializable

/**
 * 用来解决 Rxjava 不允许传递 null 值的包裹类
 *
 * @author 985892345
 * @date 2023/11/19 12:59
 */
data class Nullable<T : Any>(val value: T?): Serializable {

  val isNull: Boolean
    get() = value == null

  val isNotNull: Boolean
    get() = value != null

  inline fun nullUnless(block: (T) -> Unit): Nullable<T> {
    if (value != null) block.invoke(value)
    return this
  }

  inline fun <E> nullUnless(default: E, block: (T) -> E): E {
    return if (value != null) block.invoke(value) else default
  }

  inline fun nullIf(block: () -> Unit): Nullable<T> {
    if (value == null) block.invoke()
    return this
  }

  inline fun <E> nullIf(default: E, block: () -> E): E {
    return if (value == null) block.invoke() else default
  }
}