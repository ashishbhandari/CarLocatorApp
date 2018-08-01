package com.carlocator.data.local

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.carlocator.model.Placemarks

/**
 * @author ashish <ashish.bhandari>
 */
@Dao
interface PlaceMarkerDao {

    @Query("SELECT * FROM Placemarks")
    fun getPlaceMarkers(): LiveData<List<Placemarks>>

    @Insert
    fun insertPlaceMarkers(placeMarkers: List<Placemarks>): List<Long>

    @Query("DELETE FROM Placemarks")
    fun truncatePlaceMarkers()

}