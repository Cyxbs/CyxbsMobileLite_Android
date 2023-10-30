package com.cyxbs.pages.source.service

import androidx.room.withTransaction
import com.cyxbs.components.init.IInitialManager
import com.cyxbs.components.init.IInitialService
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.functions.api.network.AbstractDataService
import com.cyxbs.functions.api.network.internal.ISourceService
import com.cyxbs.pages.source.resquest.RequestManager
import com.cyxbs.pages.source.room.SourceDataBase
import com.cyxbs.pages.source.room.entity.RequestItemEntity
import com.g985892345.android.extensions.android.processLifecycleScope
import com.g985892345.provider.annotation.SingleImplProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/25 20:52
 */
@SingleImplProvider(ISourceService::class)
@SingleImplProvider(IInitialService::class)
object SourceServiceImpl : ISourceService, IInitialService {

  private val mDataSourceServices = ServiceManager
    .getAllSingleImpl(AbstractDataService::class)
    .mapValues { it.value.invoke() }

  override suspend fun request(
    dataSource: AbstractDataService,
    isForce: Boolean,
    values: List<String>,
  ): String {
    val name = findName(dataSource)
    val item = SourceDataBase.INSTANCE
      .requestDao
      .findItemByName(name) ?: throw IllegalStateException("不存在对应的 RequestItemEntity")
    return if (isForce || item.responseTimestamp == null ||
      System.currentTimeMillis() > item.responseTimestamp + item.interval * 60 * 60 * 1000) {
      RequestManager.request(item, values)
    } else {
      SourceDataBase.INSTANCE
        .requestDao
        .findCache(name, values)?.response
        ?: RequestManager.request(item, values)
    }
  }

  private fun findName(dataSource: AbstractDataService): String {
    mDataSourceServices.forEach {
      if (it.value == dataSource) {
        return it.key
      }
    }
    throw IllegalArgumentException("未注册的 AbstractDataSourceService")
  }

  override fun onMainProcess(manager: IInitialManager) {
    android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
      "onMainProcess")
    processLifecycleScope.launch(Dispatchers.IO) {
      android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
        "onMainProcess: launch")
      SourceDataBase.INSTANCE.apply {
        withTransaction {
          android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
            "onMainProcess: withTransaction")
          val map = mDataSourceServices.toMutableMap()
          android.util.Log.d("ggg", "(${Exception().stackTrace[0].run { "$fileName:$lineNumber" }}) -> " +
            "onMainProcess: map = ${map.map { it.key }}")
          requestDao.getItems().forEach { item ->
            val service = map.remove(item.name)
            if (service != null) {
              if (item.parameters != service.parameters) {
                requestDao.change(item.copy(parameters = service.parameters))
              }
            } else {
              requestDao.removeItem(item)
            }
          }
          map.forEach { entry ->
            requestDao.insert(RequestItemEntity(
              name = entry.key,
              interval = 12F,
              requestTimestamp = null,
              responseTimestamp = null,
              isSuccess = null,
              sort = emptyList(),
              parameters = entry.value.parameters,
              output = entry.value.output,
            ))
          }
        }
      }
    }
  }
}