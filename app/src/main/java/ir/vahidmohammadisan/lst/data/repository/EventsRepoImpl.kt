package ir.vahidmohammadisan.lst.data.repository

import android.content.SharedPreferences
import ir.vahidmohammadisan.lst.data.local.dao.EventDao
import ir.vahidmohammadisan.lst.data.local.entities.SubscriptionEntity
import ir.vahidmohammadisan.lst.data.remote.ApiService
import ir.vahidmohammadisan.lst.data.remote.dto.ApiResponse
import ir.vahidmohammadisan.lst.data.remote.dto.Event
import ir.vahidmohammadisan.lst.domain.repository.EventsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class EventsRepoImpl(
    private val sharedPreferences: SharedPreferences,
    private val apiService: ApiService,
    private val eventDao: EventDao
) : EventsRepo {

    override suspend fun getEvents(): Flow<ApiResponse<List<Event>>> = flow {
        emit(ApiResponse.Loading)

        try {
            val response = apiService.getEvents()
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun insertEvent(eventID: String) {
        eventDao.insertEvents(SubscriptionEntity(eventID = eventID))
    }

    override suspend fun isEventSubscribed(eventID: String): Boolean =
        eventDao.doesEventExist(eventId = eventID)

}