package com.carlocator.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context

/**
 * @author ashish <ashish.bhandari>
 */
class LocationUpdateModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LocationUpdateViewModel(context) as T
    }
}
