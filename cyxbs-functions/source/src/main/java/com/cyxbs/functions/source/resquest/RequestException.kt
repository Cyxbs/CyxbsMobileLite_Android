package com.cyxbs.functions.source.resquest

/**
 * 请求完全失败异常
 *
 * @author 985892345
 * @date 2023/10/26 11:46
 */
class RequestException(message: String = "请求完全失败") : RuntimeException(message) {
}