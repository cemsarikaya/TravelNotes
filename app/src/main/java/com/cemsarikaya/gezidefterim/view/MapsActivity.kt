package com.cemsarikaya.gezidefterim.view

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cemsarikaya.gezidefterim.R
import com.cemsarikaya.gezidefterim.databinding.ActivityMapsBinding
import com.cemsarikaya.gezidefterim.model.Posts
import com.cemsarikaya.gezidefterim.view.login.LoginActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    var trackBoolean: Boolean? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Posts>
    private lateinit var storage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(com.cemsarikaya.gezidefterim.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        sharedPreferences = this.getSharedPreferences("com.cemsarikaya.gezidefterim", MODE_PRIVATE)
        trackBoolean = false
        selectedLatitude = 0.0
        selectedLongitude = 0.0
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
      //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        registerLauncher()
        binding.button.visibility = View.VISIBLE
        binding.closeButton.visibility = View.INVISIBLE
        binding.button.isEnabled = false
        postArrayList = ArrayList<Posts>()

        postArrayList.clear()
        getData()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                if (!trackBoolean!!) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            //request permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                Snackbar.make(binding.root, "Permission needed for location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            val lastLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
            }
            mMap.isMyLocationEnabled = true
        }
    }

    private fun registerLauncher() {

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

                if (result) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        val lastLocation =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (lastLocation != null) {
                            val lastUserLocation =
                                LatLng(lastLocation.latitude, lastLocation.longitude)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                        }
                        mMap.isMyLocationEnabled = true
                    } else {

                        Toast.makeText(this@MapsActivity, "Permission needed!", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
    }

    override fun onMapLongClick(p0: LatLng) {

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.marker1)))
        mMap.setOnMarkerClickListener {
            it.isVisible
        }
        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
        binding.button.isEnabled = true
        binding.closeButton.visibility = View.VISIBLE


    }

    fun infoButton(view: View){


        Toast.makeText(this,"Long click for put marker on the map",Toast.LENGTH_SHORT).show()

    }

    fun addItem(view: View) {

        intent = Intent(applicationContext, AddItem::class.java)
        intent.putExtra("latitude", selectedLatitude!!.toDouble())
        intent.putExtra("longitude", selectedLongitude!!.toDouble())
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)


    }

    fun closeButton(view: View) {

        mMap.clear()
        getData()
        binding.closeButton.visibility = View.INVISIBLE
        binding.button.isEnabled = false

    }

    private fun getData() {

        val currentUser = auth.currentUser
        db.collection("Posts").whereEqualTo("userEmail", currentUser!!.email)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (value != null) {
                        if (!value.isEmpty) {
                            postArrayList.clear()
                            val documents = value.documents
                            for (document in documents) {
                                val latitude = document.get("latitude") as Double
                                val longitude = document.get("longitude") as Double
                                val latLng = LatLng(latitude, longitude)
                              mMap.addMarker(MarkerOptions().position(latLng)
                                    .icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.marker1)
                                ))
                                mMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener {

                                    val args = Bundle()
                                    args.putParcelable("ss", it.position)
                                    val intent =
                                        Intent(this@MapsActivity, DetailsActivity::class.java)
                                    intent.putExtra("bundle", args)
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                                    return@OnMarkerClickListener true
                                })
                            }
                        }
                    }
                }
            }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(com.cemsarikaya.gezidefterim.R.menu.maps_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == com.cemsarikaya.gezidefterim.R.id.signout) {
            Firebase.auth.signOut()
                val intent = Intent(this@MapsActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
        }

     return false

    }
}





