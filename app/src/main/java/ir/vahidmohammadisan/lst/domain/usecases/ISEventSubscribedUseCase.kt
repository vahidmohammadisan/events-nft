package ir.vahidmohammadisan.lst.domain.usecases

import ir.vahidmohammadisan.lst.domain.repository.EventsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ISEventSubscribedUseCase @Inject constructor(
    private val eventsRepo: EventsRepo
) {
    suspend operator fun invoke(eventID: String): Boolean {
        return eventsRepo.isEventSubscribed(eventID)
    }
}