package com.carlocator.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * @author ashish <ashish.bhandari>
 */

@Entity(tableName = "Placemarks")
data class Placemarks(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
        @ColumnInfo(name = "address") var address: String? = null,
        @ColumnInfo(name = "coordinates") val coordinates: List<Double>,
        @ColumnInfo(name = "engineType") var engineType: String = "",
        @ColumnInfo(name = "exterior") var exterior: String = "",
        @ColumnInfo(name = "interior") var interior: String = "",
        @ColumnInfo(name = "name") var name: String? = "",
        @ColumnInfo(name = "vin") var vin: String? = "")