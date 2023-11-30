package com.cyxbs.functions.source.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cyxbs.components.base.ui.CyxbsBaseViewModel
import com.cyxbs.functions.source.room.SourceDataBase
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:05
 */
class SourceViewModel : CyxbsBaseViewModel() {

  private val _requestItemContentsData: MutableLiveData<List<com.cyxbs.functions.source.data.RequestItemData>> = MutableLiveData()
  val requestItemContentsData: LiveData<List<com.cyxbs.functions.source.data.RequestItemData>> get() = _requestItemContentsData

  init {
    SourceDataBase.INSTANCE
      .requestDao
      .observeItems()
      .distinctUntilChanged()
      .collectLaunch { entities ->
        _requestItemContentsData.value = entities.map {
          com.cyxbs.functions.source.data.RequestItemData(it)
        }
      }
  }
}