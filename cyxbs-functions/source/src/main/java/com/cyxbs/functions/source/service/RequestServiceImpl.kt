package com.cyxbs.functions.source.service

import androidx.room.withTransaction
import com.cyxbs.components.init.IInitialManager
import com.cyxbs.components.init.IInitialService
import com.cyxbs.components.router.ServiceManager
import com.cyxbs.functions.api.network.AbstractDataService
import com.cyxbs.functions.api.network.internal.IRequestService
import com.cyxbs.functions.source.resquest.RequestManager
import com.cyxbs.functions.source.room.SourceDataBase
import com.cyxbs.functions.source.room.entity.RequestItemEntity
import com.g985892345.android.extensions.android.processLifecycleScope
import com.g985892345.provider.api.annotation.ImplProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/25 20:52
 */
@ImplProvider(IRequestService::class)
@ImplProvider(IInitialService::class)
object RequestServiceImpl : IRequestService, IInitialService {

  private val mDataSourceServices = ServiceManager
    .getAllImpl(AbstractDataService::class)
    .mapValues { it.value.get() }

  override suspend fun request(
    dataSource: AbstractDataService,
    isForce: Boolean,
    values: List<String>,
  ): String {
    val name = findName(dataSource)
    val item = withContext(Dispatchers.IO) {
      SourceDataBase.INSTANCE
        .requestDao
        .findItemByName(name) ?: throw IllegalStateException("不存在对应的 RequestItemEntity")
    }
    return if (isForce || item.responseTimestamp == null
      || System.currentTimeMillis() > item.responseTimestamp + item.interval * 60 * 60 * 1000
    ) {
      RequestManager.request(item, values)
    } else {
      SourceDataBase.INSTANCE
        .requestDao
        .findCache(name, values)?.response
        ?: RequestManager.request(item, values)
    }
  }

  override suspend fun getLastResponseTimestamp(dataSource: AbstractDataService): Long? {
    val name = findName(dataSource)
    return withContext(Dispatchers.IO) {
      SourceDataBase.INSTANCE
        .requestDao
        .findItemByName(name)
        ?.responseTimestamp
    }
  }

  override fun observeUpdate(dataSource: AbstractDataService): Flow<Boolean> {
    val name = findName(dataSource)
    return SourceDataBase.INSTANCE
      .requestDao
      .observeItem(name)
      .mapNotNull { it?.isSuccess }
  }


  private fun findName(dataSource: AbstractDataService): String {
    mDataSourceServices.forEach {
      if (it.value == dataSource) {
        return it.key
      }
    }
    throw IllegalArgumentException("未注册的 AbstractDataService")
  }

  override fun onMainProcess(manager: IInitialManager) {
    processLifecycleScope.launch(Dispatchers.IO) {
      SourceDataBase.INSTANCE.apply {
        withTransaction {
          // 读取所有 AbstractDataService 实现类，注册请求服务
          val map = mDataSourceServices.toMutableMap()
          requestDao.getItems().forEach { item ->
            val service = map.remove(item.name)
            if (service != null) {
              if (item.parameters != service.parameters) {
                requestDao.change(item.copy(parameters = service.parameters.map { it.key to it.value }))
              }
            } else {
              requestDao.removeItem(item)
            }
          }
          map.forEach { entry ->
            requestDao.insert(
              RequestItemEntity(
                name = entry.key,
                interval = 12F,
                requestTimestamp = null,
                responseTimestamp = null,
                isSuccess = null,
                sort = emptyList(),
                parameters = entry.value.parameters.map { it.key to it.value },
                output = entry.value.output,
              )
            )
          }
        }
      }
    }
  }
}