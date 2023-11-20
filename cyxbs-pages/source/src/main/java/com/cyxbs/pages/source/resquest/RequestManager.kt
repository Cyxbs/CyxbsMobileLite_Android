package com.cyxbs.pages.source.resquest

import androidx.room.withTransaction
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.pages.api.source.IDataSourceService
import com.cyxbs.pages.source.room.SourceDataBase
import com.cyxbs.pages.source.room.entity.RequestCacheEntity
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.cyxbs.pages.source.room.entity.RequestItemEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 23:44
 */
object RequestManager {

  suspend fun request(
    item: RequestItemEntity,
    values: List<String>,
  ): String = withContext(Dispatchers.IO) {
    if (values.size != item.parameters.size) {
      throw IllegalArgumentException("参数个数不对应，应有 ${item.parameters.size}, 实有: ${values.size}")
    }
    val parameterWithValue = buildMap {
      var index = 0
      item.parameters.forEach {
        put(it.first, values[index++])
      }
    }
    val db = SourceDataBase.INSTANCE
    val dao = db.requestDao
    val contents = dao.findContentsByName(item.name)
    if (contents.isEmpty()) throw IllegalStateException("未设置请求")
    var response: String? = null
    val requestTimestamp = System.currentTimeMillis()
    var responseTimestamp: Long? = null
    var isSuccess: Boolean? = null
    try {
      response = requestContents(contents, parameterWithValue)
      responseTimestamp = System.currentTimeMillis()
      isSuccess = true
      return@withContext response
    } catch (e: Exception) {
      isSuccess = false
      throw e
    } finally {
      db.withTransaction {
        dao.change(
          item.copy(
            requestTimestamp = requestTimestamp,
            responseTimestamp = responseTimestamp ?: item.responseTimestamp,
            isSuccess = isSuccess
          )
        )
        if (response != null) {
          dao.changeOrInsertCache(RequestCacheEntity(item.name, values, response))
        }
      }
    }
  }

  @OptIn(FlowPreview::class)
  private suspend fun requestContents(
    contents: List<RequestContentEntity>,
    parameterWithValue: Map<String, String>,
  ): String {
    val db = SourceDataBase.INSTANCE
    for (content in contents) {
      val service = ServiceManager
        .getImplOrThrow(IDataSourceService::class, content.serviceKey)
      var response: String? = null
      val requestTimestamp = System.currentTimeMillis()
      var error: Throwable? = null
      var responseTimestamp: Long? = null
      try {
        response = flow {
          emit(service.request(content.data, parameterWithValue))
        }.timeout(500.milliseconds).single()
        responseTimestamp = System.currentTimeMillis()
        return response
      } catch (e: Throwable) {
        error = e
        if (e is CancellationException) {
          throw e
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
      }
    }
    throw RequestException()
  }
}