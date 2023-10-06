package ir.vahidmohammadisan.lst.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evn_event")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val eventID: String,
)