package com.cyxbs.pages.source.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cyxbs.components.base.ui.CyxbsBaseViewModel
import com.cyxbs.pages.source.data.RequestContentItemData
import com.cyxbs.pages.source.data.RequestItemContentsData
import com.cyxbs.pages.source.room.SourceDataBase
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import com.g985892345.android.extensions.android.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 16:31
 */
class RequestContentsViewModel(
  private val name: String
) : CyxbsBaseViewModel() {

  private val _contentsData: MutableLiveData<RequestItemContentsData> = MutableLiveData()
  val contentsData: LiveData<RequestItemContentsData> get() = _contentsData

  init {
    SourceDataBase.INSTANCE
      .requestDao
      .observeItemContents(name)
      .mapNotNull { it }
      .distinctUntilChanged()
      .collectLaunch { entity ->
        _contentsData.value = RequestItemContentsData(entity.item, entity.contents)
      }
  }

  fun changeInterval(interval: Float) {
    contentsData.value
      ?.item
      ?.copy(interval = interval)
      ?.let {
        viewModelScope.launch(Dispatchers.IO) {
          SourceDataBase.INSTANCE
            .requestDao
            .change(it)
        }
      }
  }

  fun swapContentEntity(content1: RequestContentEntity, content2: RequestContentEntity): Boolean {
    val item = contentsData.value?.item ?: return false
    val index1 = item.sort.indexOf(content1.id)
    val index2 = item.sort.indexOf(content2.id)
    if (index1 >= 0 && index2 >= 0) {
      viewModelScope.launch(Dispatchers.IO) {
        SourceDataBase.INSTANCE
          .requestDao
          .change(item.copy(sort = item.sort.toMutableList().apply {
            set(index1, content2.id)
            set(index2, content1.id)
          }))
      }
      return true
    }
    return false
  }
}