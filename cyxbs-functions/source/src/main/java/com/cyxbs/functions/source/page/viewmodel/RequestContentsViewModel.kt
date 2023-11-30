package com.cyxbs.functions.source.page.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.cyxbs.components.base.ui.CyxbsBaseViewModel
import com.cyxbs.functions.source.room.SourceDataBase
import com.cyxbs.functions.source.room.entity.RequestContentEntity
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

  private val _contentsData: MutableLiveData<com.cyxbs.functions.source.data.RequestItemContentsData> = MutableLiveData()
  val contentsData: LiveData<com.cyxbs.functions.source.data.RequestItemContentsData> get() = _contentsData.distinctUntilChanged()

  init {
    SourceDataBase.INSTANCE
      .requestDao
      .observeItemContents(name)
      .mapNotNull { it }
      .distinctUntilChanged()
      .collectLaunch { entity ->
        _contentsData.value = com.cyxbs.functions.source.data.RequestItemContentsData(
          entity.item,
          entity.contents.sortedBy { entity.item.sort.indexOf(it.id) }
        )
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
    val value = contentsData.value ?: return false
    val item = value.item
    val index1 = item.sort.indexOf(content1.id)
    val index2 = item.sort.indexOf(content2.id)
    if (index1 >= 0 && index2 >= 0) {
      _contentsData.value = value.copy(contents = value.contents.toMutableList().apply {
        set(index1, content2)
        set(index2, content1)
      })
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