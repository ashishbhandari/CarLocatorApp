package com.carlocator.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.carlocator.model.LocatorConverters
import com.carlocator.model.Placemarks
import com.carlocator.utils.DATABASE_NAME

/**
 * @author ashish <ashish.bhandari>
 */
@Database(entities = [(Placemarks::class)], version = 1)
@TypeConverters(LocatorConverters::class)
abstract class LocatorDatabase : RoomDatabase() {


    abstract fun getPlaceMarkerDao(): PlaceMarkerDao

    companion object {

        @Volatile
        private var instance: LocatorDatabase? = null

        fun getInstance(context: Context): LocatorDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): LocatorDatabase {
            return Room.databaseBuilder(context, LocatorDatabase::class.java, DATABASE_NAME).build()
        }
    }

}