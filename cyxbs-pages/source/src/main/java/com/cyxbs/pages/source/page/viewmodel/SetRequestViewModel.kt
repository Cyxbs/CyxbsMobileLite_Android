package com.cyxbs.pages.source.page.viewmodel

import com.cyxbs.components.base.ui.CyxbsBaseViewModel
import com.cyxbs.pages.source.room.SourceDataBase
import com.cyxbs.pages.source.room.entity.RequestContentEntity
import kotlinx.coroutines.Dispatchers

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/21 21:18
 */
class SetRequestViewModel : CyxbsBaseViewModel() {

  fun changeOrInsertContent(newContentEntity: RequestContentEntity) {
    launch(Dispatchers.IO) {
      SourceDataBase.INSTANCE
        .requestDao
        .changeOrInsert(newContentEntity)
    }
  }

  fun removeContent(content: RequestContentEntity) {
    launch(Dispatchers.IO) {
      SourceDataBase.INSTANCE
        .requestDao
        .removeContent(content)
    }
  }
}