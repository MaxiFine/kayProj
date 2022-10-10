package com.example.ctyassistant

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.ctyassistant.databinding.ActivityNavigationMapBinding
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.NavigationSessionState
import com.mapbox.navigation.core.trip.session.NavigationSessionStateObserver
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import org.json.JSONObject
import timber.log.Timber
import java.lang.ref.WeakReference

class navigationMap : AppCompatActivity() {

    private val mapb: MapboxMap? = null

    private val mapView: MapView? = null
    private var id: String? = null
    var markerList: ArrayList<PointAnnotationOptions>? = ArrayList()
    var latitudeList: ArrayList<Double>? = ArrayList()
    var longitudeList: ArrayList<Double>? = ArrayList()
    private var databaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Places")
    private var originPosition: Point? = null
    private var destinationPosition: Point? = null
    var annotationApi: AnnotationPlugin? = null
    var pointAnnotationManager: PointAnnotationManager? = null
    lateinit var annotationConfig: AnnotationConfig
    private var originLatitude: Double? = null
    private var originLongitude: Double? = null

    private lateinit var mapboxNavigation: MapboxNavigationApp
    private val navigationLocationProvider = NavigationLocationProvider()
    private lateinit var binding: ActivityNavigationMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val  mapb = binding.mapViewN.getMapboxMap()

        binding.mapViewN.location.apply {
            setLocationProvider(navigationLocationProvider)

            // Uncomment this block of code if you want to see a circular puck with arrow.
            
            var locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    this@navigationMap,
                    R.drawable.mapbox_user_puck_icon
                )
            )

            // When true, the blue circular puck is shown on the map. If set to false, user
            // location in the form of puck will not be shown on the map.
            enabled = true
        }
        
        init()

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        binding.toolbarNavigton.setNavigationOnClickListener {
            onBackPressedMethod()
        }

        binding.startNavigation.setOnClickListener {
            // keep track of user's location
          //  fetchARoute()
        }


        val bundle: Bundle? = intent.extras
        id = intent.getStringExtra("placeId")
    }
    
    private fun init(){
        initStyle()
      //  initNavigation()
    }


    private fun initStyle(){
        binding.mapViewN.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {

                    annotationApi = mapView?.annotations

                    var layId = "map annotation"
                    annotationConfig = AnnotationConfig(
                        layerId = layId
                    )

                    pointAnnotationManager =
                        annotationApi?.createPointAnnotationManager(annotationConfig)

                    addAnnotation()

                    var locationPermissionHelper =
                        LocationPermissionHelper(WeakReference(this@navigationMap))
                    locationPermissionHelper.checkPermissions {
                        onMapReady()
                    }

                }
            }
        )
    }

    private fun updateCamera(location: android.location.Location) {
        val mapAnimationOptions = MapAnimationOptions.Builder().duration(1500L).build()
        binding.mapViewN.camera.easeTo(
            CameraOptions.Builder()
                // Centers the camera to the lng/lat specified.
                .center(Point.fromLngLat(location.longitude, location.latitude))
                // specifies the zoom value. Increase or decrease to zoom in or zoom out
                .zoom(12.0)
                // specify frame of reference from the center.
                .padding(EdgeInsets(500.0, 0.0, 0.0, 0.0))
                .build(),
            mapAnimationOptions
        )
    }


    private fun onMapReady() {
        mapView?.getMapboxMap()?.setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            //    initLocationComponent()
            //  setupGesturesListener()
        }
    }

    // pin on locations
    private fun addAnnotation() {

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (d in snapshot.children) {
                        val locss = snapshot.getValue(constAddPlace::class.java)

                        if (locss != null) {
                            latitudeList?.add(locss.lat!!)
                        }
                        if (locss != null) {
                            longitudeList?.add(locss.lng!!)
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag(tag).d(error.toString())
            }

        })

        val bitmap = convertDrawableToBitmap(
            AppCompatResources.getDrawable(
                this@navigationMap,
                R.drawable.ic_baseline_location_on_24blue
            )
        )


        //loop through the latitudes and longitudes
        for (i in 0 until (markerList?.size!!)) {

            val jsonObject = JSONObject();
            jsonObject.put("somevalue", latitudeList?.get(i))

            //draw a pin
            // Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                // Define a geographic coordinate.
                .withPoint(Point.fromLngLat(longitudeList!!.get(i), latitudeList!!.get(i)))
                .withData(Gson().fromJson(jsonObject.toString(), JsonElement::class.java))
                // Specify the bitmap you assigned to the point annotation
                // The bitmap will be added to map style automatically.
                .withIconImage(bitmap!!)

            markerList!!.add(pointAnnotationOptions)
            // Add the resulting pointAnnotation to the map.

        }
        pointAnnotationManager?.create(markerList!!)
    }

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    //get Directions


    override fun onStart() {
        super.onStart()

        databaseRef.child(id.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ct = snapshot.getValue(constAddPlace::class.java)

                originLongitude = ct?.lng
                originLatitude = ct?.lat

            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag(mapActivity.tag).d(error.message)
            }
        })
    }

    private fun toMain() {
        intent = Intent(this@navigationMap, mapActivity::class.java)
        startActivity(intent)
    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                //method here
                toMain()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    toMain()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }


}