package com.cyxbs.pages.source.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cyxbs.components.base.ui.CyxbsBaseViewModel
import com.cyxbs.pages.source.data.RequestItemContentsData
import com.cyxbs.pages.source.room.SourceDataBase
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:05
 */
class SourceViewModel : CyxbsBaseViewModel() {

  private val _requestItemContentsData: MutableLiveData<List<RequestItemContentsData>> = MutableLiveData()
  val requestItemContentsData: LiveData<List<RequestItemContentsData>> get() = _requestItemContentsData

  init {
    SourceDataBase.INSTANCE
      .requestDao
      .observeAll()
      .distinctUntilChanged()
      .collectLaunch { entities ->
        _requestItemContentsData.value =
          entities.map { entity ->
            RequestItemContentsData(
              entity.item,
              entity.contents.sortedBy {
                // 按顺序显示
                entity.item.sort.indexOf(it.id)
              }
            )
          }
      }
  }
}