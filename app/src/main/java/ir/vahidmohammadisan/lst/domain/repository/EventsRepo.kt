package ir.vahidmohammadisan.lst.domain.repository

import ir.vahidmohammadisan.lst.data.remote.dto.ApiResponse
import ir.vahidmohammadisan.lst.data.remote.dto.Event
import kotlinx.coroutines.flow.Flow

interface EventsRepo {

    suspend fun getEvents(): Flow<ApiResponse<List<Event>>>

    suspend fun insertEvent(eventID: String)

    suspend fun isEventSubscribed(eventID: String): Boolean

}