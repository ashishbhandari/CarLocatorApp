package com.carlocator.model

import android.arch.persistence.room.TypeConverter

/**
 * @author ashish <ashish.bhandari>
 */
class LocatorConverters {

    @TypeConverter
    fun fromCoordinates(data: List<Double>?): String? {
        return data?.joinToString(",")
    }

    @TypeConverter
    fun toCoordinates(data: String?): List<Double>? {
        return data?.let {
            it.split(",").map {
                try {
                    it.toDouble()
                } catch (ex: NumberFormatException) {
                    null
                }
            }
        }?.filterNotNull()
    }
}