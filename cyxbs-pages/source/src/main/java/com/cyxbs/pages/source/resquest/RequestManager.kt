package com.cyxbs.pages.source.resquest

import com.cyxbs.pages.source.room.SourceDataBase
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.cyxbs.pages.source.room.entity.RequestItemEntity
import com.g985892345.android.extensions.android.processLifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 23:44
 */
object RequestManager {

  private const val CAPACITY = 10

  private val mWaitChanel = Channel<RequestUnit>(CAPACITY).apply {
    repeat(CAPACITY) {
      trySend(RequestUnit())
    }
  }

  suspend fun request(
    item: RequestItemEntity,
    values: List<String>,
  ): String = withContext(Dispatchers.IO) {
    val dao = SourceDataBase.INSTANCE.requestDao
    val contents = dao.findContentsByName(item.name)
    if (contents.isEmpty()) throw IllegalStateException("未设置请求")
    var response: String? = null
    val requestTimestamp = System.currentTimeMillis()
    var responseTimestamp: Long? = null
    var isSuccess: Boolean? = null
    try {
      response = requestContents(contents, item.parameters.map { it.first }, values)
      responseTimestamp = System.currentTimeMillis()
      isSuccess = true
      return@withContext response
    } catch (e: Exception) {
      isSuccess = false
      if (e is CancellationException) {
        isSuccess = null
      }
      throw e
    } finally {
      dao.change(
        item.copy(
          response = response,
          requestTimestamp = requestTimestamp,
          responseTimestamp = responseTimestamp,
          isSuccess = isSuccess
        )
      )
    }
  }

  private suspend fun requestContents(
    contents: List<RequestContentEntity>,
    parameters: List<String>,
    values: List<String>,
  ): String {
    val db = SourceDataBase.INSTANCE
    for (content in contents) {
      val unit = getFreeRequestUnit()
      var response: String? = null
      val requestTimestamp = System.currentTimeMillis()
      var error: String? = null
      var responseTimestamp: Long? = null
      try {
        val url = replaceValue(content.url, parameters, values)
        val js = replaceValue(content.js, parameters, values)
        response = unit.load(url, js)
        responseTimestamp = System.currentTimeMillis()
        return response
      } catch (e: Exception) {
        if (e is CancellationException) {
          error = "协程被取消, message = ${e.message}"
          throw e
        } else {
          error = e.message
        }
      } finally {
        db.requestDao.change(
          content.copy(
            response = response,
            error = error,
            requestTimestamp = requestTimestamp,
            responseTimestamp = responseTimestamp,
          )
        )
        // 放进缓存池
        putFreeRequestUnit(unit)
      }
    }
    throw RequestException()
  }

  private suspend fun getFreeRequestUnit(): RequestUnit {
    return mWaitChanel.receive()
  }

  private fun putFreeRequestUnit(unit: RequestUnit) {
    processLifecycleScope.launch(Dispatchers.IO) {
      mWaitChanel.send(unit)
    }
  }

  fun replaceValue(
    string: String?,
    parameters: List<String>,
    values: List<String>,
  ): String? {
    if (parameters.size < values.size) {
      throw IllegalArgumentException("parameters.size 小于了 value.size")
    }
    if (parameters.isEmpty()) return string
    var result = string ?: return null
    values.forEachIndexed { index, s ->
      result = result.replace("{${parameters[index]}}", s)
    }
    return result
  }
}