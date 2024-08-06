package com.mdev1008.nutriscanandroiddev.models.remote

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.mdev1008.nutriscanandroiddev.models.data.SearchHistoryItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(entities = [
    User::class,
    SearchHistoryItem::class,
    UserDietaryPreference::class,
    UserDietaryRestriction::class,
    UserAllergen::class], version = 1)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase:RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun userDietaryPreferenceDao(): UserDietaryPreferenceDao
    abstract fun userRestrictionDao(): UserDietaryRestrictionDao
    abstract fun userAllergenDao(): UserAllergenDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DateTimeConverter{
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String?{
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime?{
        return value?.let {
            LocalDateTime.parse(it, formatter)
        }
    }
}