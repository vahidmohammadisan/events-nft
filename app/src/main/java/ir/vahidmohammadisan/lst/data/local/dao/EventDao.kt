package ir.vahidmohammadisan.lst.data.local.dao

import androidx.room.*
import ir.vahidmohammadisan.lst.data.local.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(event: SubscriptionEntity)

    @Query("SELECT EXISTS (SELECT 1 FROM evn_event WHERE eventId = :eventId)")
    fun doesEventExist(eventId: String): Boolean

}