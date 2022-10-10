package com.example.ctyassistant

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.BaseMmsColumns.PRIORITY
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ctyassistant.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.observable.model.RenderMode
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOwnerRegistry.COMPASS
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.*
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import org.json.JSONObject
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.collections.ArrayList


class mapActivity : AppCompatActivity() {


    // var permissionsManager: PermissionsManager? = null

    private val mapb: MapboxMap? = null

    private val callback = LocationListeningCallback(this@mapActivity)


    var annotationApi: AnnotationPlugin? = null
    var pointAnnotationManager: PointAnnotationManager? = null
    lateinit var annotationConfig: AnnotationConfig

    private lateinit var locationPermissionHelper: LocationPermissionHelper

    var markerList: ArrayList<PointAnnotationOptions>? = ArrayList()

    var latitudeList: ArrayList<Double>? = ArrayList()
    var longitudeList: ArrayList<Double>? = ArrayList()
    var placeId: ArrayList<String>? = ArrayList()


    lateinit var mapLocations: ArrayList<String>

    private var listView: ListView? = null
    var arrayAdapter: ArrayAdapter<String>? = null

    private lateinit var mapView: MapView
    private lateinit var binding: ActivityMapBinding

    private var databaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Places")


    companion object {
        const val tag = "map activity"
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    var llat: Double? = null
    var llng: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = findViewById(R.id.mapView)

        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS,
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {

                    annotationApi = mapView.annotations

                    val layId = "map annotation"
                    annotationConfig = AnnotationConfig(
                        layerId = layId
                    )

                    pointAnnotationManager =
                        annotationApi?.createPointAnnotationManager(annotationConfig)
                    addAnnotation()
                }
            }
        )

        // in onCreate() initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        setSupportActionBar(binding.mapToolbar)


        mapLocations = ArrayList<String>()

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mapLocations)

        binding.listView.adapter = arrayAdapter

        binding.getCurDir.setOnClickListener {

            showCurrentLocation()
        }

        binding.direButton.setOnClickListener {

            // function to get location

            // getLastLocation()
            //toDire()

        }

    }


    private fun clearAnnotation() {
        markerList = ArrayList()

        pointAnnotationManager?.deleteAll()
    }


    // pin on locations
    private fun addAnnotation() {

        // add on click listener to the pins on the map
        clearAnnotation()

        pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener { annotation: PointAnnotation ->
            onALocationPressed(annotation)
            true
        })


        var bitmap = convertDrawableToBitmap(
            AppCompatResources.getDrawable(
                this@mapActivity,
                R.drawable.ic_baseline_location_on_24blue
            )
        )

        //loop through the latitudes and longitudes
        for (i in 0 until (markerList?.size!!)) {

            val jsonObject = JSONObject();
            jsonObject.put("somevalue", placeId?.get(i))

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


    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val i = menuInflater
        i.inflate(R.menu.menu, menu)

        //  val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.actionsearch)
        val searchView = searchItem?.actionView as SearchView

        // searchView.queryHint = getString(R.string.search)
        // searchView.findViewById<AutoCompleteTextView>(R.id.sea)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.feedbSett -> {
                val t = Intent(this@mapActivity, feedback::class.java)
                startActivity(t)
                return true
            }
            R.id.addlocSett -> {
                val e = Intent(this@mapActivity, adminLogIn::class.java)
                startActivity(e)

                return true
            }
            R.id.LogoutSett -> {
                onBackPressedMethod()

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                //method here
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        }
    }

    // will show info of a place
    private fun onALocationPressed(marker: PointAnnotation): Boolean {

        val jsonElement: JsonElement? = marker.getData()

        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.onpinclicked, null)
        builder.setView(view)
        val dialog = builder.create()

        val close = view.findViewById<ImageButton>(R.id.closebtnLocInfo)
        val image = view.findViewById<ImageView>(R.id.imageLocInfo)
        val directions = view.findViewById<AppCompatButton>(R.id.directionLocInfo)
        val name = view.findViewById<TextView>(R.id.nameLocInfo)
        val typeOfInstitution = view.findViewById<TextView>(R.id.typeOfInstitutionLocInfo)

        databaseRef.child(jsonElement.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val ct = snapshot.getValue(constAddPlace::class.java)

                    name.setText(ct?.nameOfPlace);
                    typeOfInstitution.setText(ct?.typeOfInstitution);

                    Glide
                        .with(view)
                        .load(ct?.imageUri.toString())
                        .into(image);
                }

                @Override
                override fun onCancelled(error: DatabaseError) {

                    Timber.tag(com.example.ctyassistant.tag).d(error.toString())
                }
            })


        close.setOnClickListener {

            dialog.dismiss()
        }

        directions.setOnClickListener {
            val i = Intent(this, searchRoute::class.java)
            i.putExtra("placeId", jsonElement.toString())
            startActivity(i)
        }

        dialog.show()

        return true
    }


    /*
    private fun locationEngines() {

        val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
        val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

        val locationEngine: LocationEngine =
            LocationEngineProvider.getBestLocationEngine(this)

        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }
        locationEngine.requestLocationUpdates(request, callback, mainLooper)
        locationEngine.getLastLocation(callback)

    }

     */

    private fun showCurrentLocation() {
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this@mapActivity))
        locationPermissionHelper.checkPermissions {
            //  onMapReady()
        }

    }

    private fun initLocationComponent() {

        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@mapActivity,
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@mapActivity,
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }


    }

    /*
    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            enableLocationComponent()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }



    private fun onCameraTrackingDismissed() {
        // Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


       // locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("Not yet implemented")
        // Timber.tag(tag).d("user didn't allow permission")
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent()
        } else {
            Timber.tag(tag).d("permissions were denied")
            finish()
        }
    }

     */


    fun toDire() {

        intent = Intent(this@mapActivity, searchRoute::class.java)
        startActivity(intent)
    }


    private fun getDataa(snapshot: DataSnapshot) {
        for (d in snapshot.children) {
            val p = d.getValue(constAddPlace::class.java)

            var t: Double? = p?.lat
            var o: Double? = p?.lng
            var w: String? = p?.placeId

            if (p != null) {

                if (t != null) {
                    latitudeList?.add(t)
                }
            }
            if (p != null) {
                if (o != null) {
                    longitudeList?.add(o)
                }
            }
            if (p != null) {
                if (w != null) {
                    placeId?.add(w)
                }
            }
        }
    }

    // life cycle
    override fun onStart() {
        super.onStart()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    getDataa(snapshot)

                    for (ds in snapshot.getChildren()) {
                        val l = ds.getValue(constAddPlace::class.java)
                        val o = l?.nameOfPlace

                        if (o != null) {
                            mapLocations.add(o)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag(tag).d(error.message)
            }
        })
    }


    @SuppressLint("Lifecycle")
    override fun onStop() {
        super.onStop()

        /*
        val locationEngine: LocationEngine? = null
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback)
        }

         */

        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}
