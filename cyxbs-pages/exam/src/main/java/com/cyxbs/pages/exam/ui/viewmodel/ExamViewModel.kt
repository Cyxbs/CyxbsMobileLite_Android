package com.cyxbs.pages.exam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cyxbs.components.base.ui.CyxbsBaseViewModel
import com.cyxbs.components.router.impl
import com.cyxbs.functions.api.account.IAccountService
import com.cyxbs.pages.exam.bean.ExamBean
import com.cyxbs.pages.exam.bean.toExamBean
import com.cyxbs.pages.exam.model.ExamRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * .
 *
 * @author 985892345
 * 2023/4/8 16:53
 */
class ExamViewModel : CyxbsBaseViewModel() {
  
  private val _examBean = MutableLiveData<List<ExamBean>>()
  val examBean: LiveData<List<ExamBean>> get() = _examBean
  
  private val _refreshEvent = MutableSharedFlow<Throwable?>()
  val refreshEvent: SharedFlow<Throwable?> get() = _refreshEvent
  
  fun refreshExam() {
    val stuNum = IAccountService::class.impl.getStuNum()
    if (stuNum == null) {
      viewModelScope.launch {
        _refreshEvent.emit(IllegalStateException("学号为空"))
      }
    } else {
      launch {
        runCatching {
          ExamRepository.refreshLesson(stuNum, true)
        }.onSuccess {
          _refreshEvent.emit(null)
        }.onFailure {
          _refreshEvent.emit(it)
        }
      }
    }
  }
  
  init {
    ExamRepository.observeSelfExam()
      .collectLaunch {
        _examBean.postValue(it.toExamBean())
      }
  }
}