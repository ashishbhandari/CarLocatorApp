package com.carlocator.model

import com.google.gson.annotations.SerializedName

/**
 * @author ashish <ashish.bhandari>
 */
data class PlacemarksSource(
        @SerializedName("placemarks") var placeMarks: List<Placemarks> = emptyList()
)