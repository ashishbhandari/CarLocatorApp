package com.carlocator.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import pub.devrel.easypermissions.EasyPermissions


/**
 * @author ashish <ashish.bhandari>
 */
class LocationLiveData(val context: Context) : LiveData<Location>() {

    private val TAG = LocationLiveData::class.java.name

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()

        if (!hasLocationPermission()) {
            return
        }

        val locationProviderClient = getFusedLocationProviderClient()
        getLastLocation()
        locationProviderClient?.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.myLooper())
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient?.lastLocation?.addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful && task.result != null) {
                Log.i(TAG, task.result.latitude.toString() + " : " + task.result.longitude.toString())
                value = task.result
            } else {
                Log.w(TAG, "getLastLocation:exception", task.exception)
            }
        }
    }

    private fun createLocationRequest(): LocationRequest {
        var locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

    private fun getFusedLocationProviderClient(): FusedLocationProviderClient? {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        }
        return fusedLocationProviderClient
    }

    override fun onInactive() {
        super.onInactive()
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val newLocation = locationResult.lastLocation
            Log.i(TAG, newLocation.latitude.toString() + " : " + newLocation.longitude.toString())
            value = newLocation
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}