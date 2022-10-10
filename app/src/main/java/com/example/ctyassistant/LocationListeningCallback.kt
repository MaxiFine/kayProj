package com.example.ctyassistant

import android.app.Activity
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import java.lang.ref.WeakReference

class LocationListeningCallback internal constructor(activity: Activity) :
    LocationEngineCallback<LocationEngineResult> {

    private val activityWeakReference: WeakReference<Activity>

    init {
        this.activityWeakReference = WeakReference(activity)
    }

    override fun onSuccess(result: LocationEngineResult) {

        // The LocationEngineCallback interface's method which fires when the device's location has changed.

        result.getLastLocation()

    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location can not be captured
     *
     * @param exception the exception message
     */

    override fun onFailure(exception: Exception) {

        // The LocationEngineCallback interface's method which fires when the device's location can not be captured

    }
}

