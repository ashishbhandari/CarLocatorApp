package com.carlocator.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.carlocator.data.LocatorRepository
import com.carlocator.data.network.Resource
import com.carlocator.model.Placemarks


/**
 * @author ashish <ashish.bhandari>
 *
 * This is responsible for providing the data for a specific UI components such as fragment, activity.
 * It handles the communication with the business part of data handling.
 */
class LocatorViewModel(repository: LocatorRepository) : ViewModel() {

    private var placeMarkers: LiveData<Resource<List<Placemarks>?>>

    init {
        placeMarkers = repository.getPlaceMarkers()
    }

    /**
     * Return place markers to observe on the UI.
     */
    fun getPlaceMarkers() = placeMarkers

}