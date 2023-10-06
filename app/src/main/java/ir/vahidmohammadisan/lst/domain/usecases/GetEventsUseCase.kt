package ir.vahidmohammadisan.lst.domain.usecases

import ir.vahidmohammadisan.lst.data.remote.dto.ApiResponse
import ir.vahidmohammadisan.lst.data.remote.dto.Event
import ir.vahidmohammadisan.lst.domain.repository.EventsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventsRepo: EventsRepo
) {
    suspend operator fun invoke(): Flow<ApiResponse<List<Event>>> {
        return eventsRepo.getEvents()
    }
}