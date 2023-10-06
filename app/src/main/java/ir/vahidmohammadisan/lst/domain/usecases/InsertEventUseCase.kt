package ir.vahidmohammadisan.lst.domain.usecases

import ir.vahidmohammadisan.lst.domain.repository.EventsRepo
import javax.inject.Inject

class InsertEventUseCase @Inject constructor(
    private val eventsRepo: EventsRepo
) {
    suspend operator fun invoke(eventID: String) {
        eventsRepo.insertEvent(eventID)
    }
}