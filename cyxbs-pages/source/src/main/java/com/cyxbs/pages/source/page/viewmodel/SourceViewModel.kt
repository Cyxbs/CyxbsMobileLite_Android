package com.cyxbs.pages.source.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cyxbs.components.base.ui.CyxbsBaseViewModel
import com.cyxbs.pages.source.data.RequestItemData
import com.cyxbs.pages.source.room.SourceDataBase
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 15:05
 */
class SourceViewModel : CyxbsBaseViewModel() {

  private val _requestItemContentsData: MutableLiveData<List<RequestItemData>> = MutableLiveData()
  val requestItemContentsData: LiveData<List<RequestItemData>> get() = _requestItemContentsData

  init {
    SourceDataBase.INSTANCE
      .requestDao
      .observeItems()
      .distinctUntilChanged()
      .collectLaunch { entities ->
        _requestItemContentsData.value = entities.map {
          RequestItemData(it)
        }
      }
  }
}