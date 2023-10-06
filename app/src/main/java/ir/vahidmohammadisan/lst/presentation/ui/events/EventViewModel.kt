package ir.vahidmohammadisan.lst.presentation.ui.events

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.vahidmohammadisan.lst.data.remote.dto.ApiResponse
import ir.vahidmohammadisan.lst.data.remote.dto.Event
import ir.vahidmohammadisan.lst.domain.usecases.GetEventsUseCase
import ir.vahidmohammadisan.lst.domain.usecases.ISEventSubscribedUseCase
import ir.vahidmohammadisan.lst.domain.usecases.InsertEventUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val isEventSubscribedUseCase: ISEventSubscribedUseCase,
) : ViewModel() {

    private val _eventLiveData = MutableLiveData<ApiResponse<List<Event>>>()
    val eventLiveData: MutableLiveData<ApiResponse<List<Event>>>
        get() = _eventLiveData

    private val _isEventSubscribed = MutableLiveData<Boolean>()
    val isEventSubscribed: MutableLiveData<Boolean>
        get() = _isEventSubscribed

    fun getEvents() {
        viewModelScope.launch {
            getEventsUseCase.invoke().collect {
                _eventLiveData.value = it
            }
        }
    }

    fun insertEvent(eventID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            insertEventUseCase.invoke(eventID)
        }
    }

    fun isEventSubscribed(eventID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isEventSubscribed.postValue(isEventSubscribedUseCase.invoke(eventID))
        }
    }

}