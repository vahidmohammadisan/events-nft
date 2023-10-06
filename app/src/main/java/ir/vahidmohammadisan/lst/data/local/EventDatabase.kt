package ir.vahidmohammadisan.lst.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.vahidmohammadisan.lst.data.local.dao.EventDao
import ir.vahidmohammadisan.lst.data.local.entities.SubscriptionEntity

@Database(entities = [SubscriptionEntity::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDoa(): EventDao
}