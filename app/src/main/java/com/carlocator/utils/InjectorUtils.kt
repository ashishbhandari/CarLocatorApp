package com.carlocator.utils

import android.content.Context
import com.carlocator.data.LocatorRepository
import com.carlocator.data.local.LocatorDatabase
import com.carlocator.data.remote.LocatorService
import com.carlocator.ui.LocationUpdateModelFactory
import com.carlocator.ui.LocatorViewModelFactory

/**
 * @author ashish <ashish.bhandari>
 *
 * Static methods used to inject classes needed for various Activities and Fragments.
 *
 */
object InjectorUtils {

    private fun getLocatorRepository(context: Context): LocatorRepository {

        return LocatorRepository(LocatorDatabase.getInstance(context).getPlaceMarkerDao(), LocatorService.getLocatorService())
    }

    fun provideLocatorViewModelFactory(context: Context): LocatorViewModelFactory {
        val repository = getLocatorRepository(context.applicationContext)
        return LocatorViewModelFactory(repository)
    }

    fun provideLocationUpdateModelFactory(context: Context) : LocationUpdateModelFactory{
        return LocationUpdateModelFactory(context)
    }

}