package com.carlocator.ui.fragment

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.carlocator.R
import com.carlocator.model.Placemarks
import com.carlocator.ui.LocationUpdateViewModel
import com.carlocator.ui.LocatorViewModel
import com.carlocator.utils.InjectorUtils
import com.carlocator.utils.OnMapAndViewReadyListener
import com.carlocator.utils.RC_LOCATION_PERMISSION_FINE
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


/**
 * @author ashish <ashish.bhandari>
 */
class MapUIFragment : Fragment(),
        EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private val TAG = MapUIFragment::class.java.name


    private lateinit var viewModel: LocatorViewModel
    private lateinit var viewModelLocation: LocationUpdateViewModel
    private lateinit var mGoogleMap: GoogleMap


    private val USER_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private var userCurrentLocation: LatLng? = null

    private var data: MutableMap<String?, Placemarks>? = null

    private var lastSelectedPlaceMark: Placemarks? = null


    companion object {
        fun newInstance(): MapUIFragment {
            return MapUIFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_map, container, false)
        val context = context ?: return view

        val factory = InjectorUtils.provideLocatorViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(LocatorViewModel::class.java)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)

        val locationUpdateFactory = InjectorUtils.provideLocationUpdateModelFactory(context)

        viewModelLocation = ViewModelProviders.of(this, locationUpdateFactory).get(LocationUpdateViewModel::class.java)

        subscribePlaceMarkers()


        if (hasLocationPermission()) {
            subscribeUserLocationUpdates()
        } else {
            requestPermission()
        }

        return view
    }

    private fun subscribePlaceMarkers() {
        viewModel.getPlaceMarkers().observe(viewLifecycleOwner, Observer { placeMarkers ->
            if (placeMarkers?.data != null) {
                val listOfMarkers = placeMarkers.data

                val mapOfPlaceMarkers = listOfMarkers?.associate { it.vin to it }
                this.data = mapOfPlaceMarkers?.toMutableMap()
                stuffOnMapReady(data)
            }
        })
    }


    /**
     * calculate bounds and place marker only when map is ready
     *
     */
    private fun stuffOnMapReady(data: Map<String?, Placemarks>?) {
        if(data == null || data.isEmpty()) return
        checkReadyThen {
            val boundsBuilder = LatLngBounds.Builder()
            data?.keys?.map { place ->
                val placemarks = data?.getValue(place)
                val latLng = LatLng(placemarks.coordinates[1], placemarks.coordinates[0])
                boundsBuilder.include(latLng)
            }

            if (userCurrentLocation != null) {
                boundsBuilder.include(userCurrentLocation)
            }

            val bounds = boundsBuilder.build()

            with(mGoogleMap) {
                // Hide the zoom controls as the button panel will cover it.
                uiSettings.isZoomControlsEnabled = false

                // Set listeners for marker events.  See the bottom of this class for their behavior.
                setOnMarkerClickListener(this@MapUIFragment)
                setOnInfoWindowClickListener(this@MapUIFragment)

                setOnInfoWindowCloseListener(this@MapUIFragment)
                setOnInfoWindowLongClickListener(this@MapUIFragment)

                // Override the default content description on the view, for accessibility mode.
                // Ideally this string would be localised.
                setContentDescription("Map with lots of markers.")

                moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(bounds, 50))
            }


            // add multiple markers
            addMarkersToMap()
        }
    }

    private fun addMarkersToMap() {
        // place markers for each of the defined locations
        data?.keys?.map {
            with(data?.getValue(it)) {
                val latLng = LatLng(this!!.coordinates[1], this!!.coordinates[0])
                mGoogleMap.addMarker(com.google.android.gms.maps.model.MarkerOptions()
                        .position(latLng)
                        .title(this?.address)
                        .snippet(this?.vin)
                        .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker())
                        .infoWindowAnchor(0.5F, 0F)
                        .draggable(false)
                        .zIndex(0F))

            }
        }

        // place marker for current user location if available
        if (userCurrentLocation != null) {

            mGoogleMap.addMarker(userCurrentLocation?.let {
                com.google.android.gms.maps.model.MarkerOptions()
                        .position(userCurrentLocation!!)
                        .title("Current Position")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
                        .infoWindowAnchor(0.5F, 0F)
                        .draggable(false)
                        .zIndex(0F)
            })
        }


    }

    private fun subscribeUserLocationUpdates() {
        viewModelLocation.getLocationLiveData().observe(viewLifecycleOwner, Observer { location ->
            if (location != null) {
                Log.i("value", location.latitude.toString() + " : " + location.longitude.toString())
                val userCurrentLocation = LatLng(location.latitude, location.longitude)
                this.userCurrentLocation = userCurrentLocation
            }
        })
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(activity as Activity, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermission() {
        EasyPermissions.requestPermissions(activity as Activity,
                "App needs access to your location to know where you are.",
                RC_LOCATION_PERMISSION_FINE,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("MainActivity", "onPermissionsDenied:" + requestCode + ":" + perms.size)

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            activity?.let { AppSettingsDialog.Builder(it).build().show() }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("MainActivity", "onPermissionsGranted:" + requestCode + ":" + perms.size)
        subscribeUserLocationUpdates()
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, "onRationaleDenied:")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, "onRationaleAccepted:")
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        var currentSelectedPlaceMarker: Placemarks? = data?.get(marker.snippet) ?: return true

        if (lastSelectedPlaceMark == null) {
            lastSelectedPlaceMark = currentSelectedPlaceMarker
            checkReadyThen { mGoogleMap.clear() }

            if (lastSelectedPlaceMark != null) {
                val latLng = LatLng(lastSelectedPlaceMark!!.coordinates[1], lastSelectedPlaceMark!!.coordinates[0])
                val addMarker = mGoogleMap.addMarker(com.google.android.gms.maps.model.MarkerOptions()
                        .position(latLng)
                        .title(lastSelectedPlaceMark?.address)
                        .snippet(lastSelectedPlaceMark?.vin)
                        .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker())
                        .infoWindowAnchor(0.5F, 0F)
                        .draggable(false)
                        .zIndex(0F))
                addMarker.showInfoWindow()
            }
        } else if (lastSelectedPlaceMark!!.vin == currentSelectedPlaceMarker?.vin) {
            checkReadyThen { mGoogleMap.clear() }
            stuffOnMapReady(data)
            lastSelectedPlaceMark = null
            return true
        }
        return false
    }


    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(activity, "Click Info Window", Toast.LENGTH_SHORT).show()
    }

    override fun onInfoWindowLongClick(p0: Marker?) {
        Toast.makeText(activity, "Long Click Info Window", Toast.LENGTH_SHORT).show()
    }

    override fun onInfoWindowClose(p0: Marker?) {
        Toast.makeText(activity, "close Info Window", Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.mGoogleMap = googleMap ?: return

        stuffOnMapReady(data)
    }


    /**
     * Checks if the mGoogleMap is ready, the executes the provided lambda function
     *
     * @param proceedToMoveAhead the code to be executed if the mGoogleMap is ready
     */
    private fun checkReadyThen(proceedToMoveAhead: () -> Unit) {
        if (!::mGoogleMap.isInitialized) {
            Toast.makeText(activity, getString(R.string.map_not_ready), Toast.LENGTH_SHORT).show()
        } else {
            proceedToMoveAhead()
        }
    }
}