package com.carlocator.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.location.Location

/**
 * @author ashish <ashish.bhandari>
 */
class LocationUpdateViewModel(context: Context) : ViewModel() {

    private var locationLiveData: LiveData<Location>


    init {
        locationLiveData =  LocationLiveData(context)
    }



    /**
     * Return location updates to observe on the UI.
     */
    fun getLocationLiveData() = locationLiveData
}