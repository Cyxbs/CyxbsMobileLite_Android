package com.cyxbs.functions.api.script

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/31 08:03
 */
interface IWebViewSpiderService {

  /**
   * 获取某网页内容
   */
  suspend fun load(url: String): String

  /**
   * 加载某网页后执行 [js] 代码
   *
   * js 中可调用
   * - cyxbsBridge.success(String)   传递成功结果
   * - cyxbsBridge.error(String)     传递失败结果
   */
  suspend fun load(url: String, js: String): String

  /**
   * 使用 webView 执行 [js] 代码，webView 直接执行 [js] 存在一定缺陷，部分请求会因为浏览器同源策略而请求失败
   *
   * js 中可调用
   * - cyxbsBridge.success(String)   传递成功结果
   * - cyxbsBridge.error(String)     传递失败结果
   */
  suspend fun evaluateJs(js: String): String
}