package ir.vahidmohammadisan.lst.data.remote

import ir.vahidmohammadisan.lst.data.remote.dto.Event
import retrofit2.http.GET

interface ApiService {
    @GET("events")
    suspend fun getEvents(): List<Event>
}