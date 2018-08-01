package com.carlocator.data

import android.arch.lifecycle.LiveData
import com.carlocator.AppExecutors
import com.carlocator.data.local.PlaceMarkerDao
import com.carlocator.data.network.Resource
import com.carlocator.data.remote.LocatorService
import com.carlocator.model.Placemarks
import com.carlocator.model.PlacemarksSource


/**
 * @author ashish <ashish.bhandari>
 *
 * This module responsible for handling data operations. They provide clean API to rest of the App.
 */
class LocatorRepository(
        private val placeMarkerDao: PlaceMarkerDao,
        private val locatorService: LocatorService,
        private val appExecutors: AppExecutors = AppExecutors()
) {

    fun getPlaceMarkers(): LiveData<Resource<List<Placemarks>?>> {

        return object : NetworkBoundResource<List<Placemarks>, PlacemarksSource>(appExecutors) {

            override fun saveCallResult(item: PlacemarksSource) {
                placeMarkerDao.truncatePlaceMarkers()
                placeMarkerDao.insertPlaceMarkers(item.placeMarks)
            }

            override fun shouldFetch(data: List<Placemarks>?): Boolean = true


            override fun loadFromDb(): LiveData<List<Placemarks>>{
                return placeMarkerDao.getPlaceMarkers()
            }

            override fun createCall(): LiveData<Resource<PlacemarksSource>> {
                return locatorService.getPlaceMarkersSource()
            }


        }.asLiveData()
    }

}
