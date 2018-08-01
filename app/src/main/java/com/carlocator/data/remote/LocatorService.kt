package com.carlocator.data.remote

import android.arch.lifecycle.LiveData
import com.carlocator.data.network.Resource
import com.carlocator.model.PlacemarksSource
import com.carlocator.utils.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * @author ashish <ashish.bhandari>
 */
interface LocatorService {

    @GET("wunderbucket/locations.json")
    fun getPlaceMarkersSource(): LiveData<Resource<PlacemarksSource>>


    companion object Factory {
        private const val BASE_URL = "https://s3-us-west-2.amazonaws.com/"

        fun getLocatorService(): LocatorService {
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .build()
                    .create(LocatorService::class.java)
        }
    }
}