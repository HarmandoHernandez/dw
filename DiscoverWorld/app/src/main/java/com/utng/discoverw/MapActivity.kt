package com.utng.discoverw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var title: String
    private var lat: Double = 21.15794952216306
    private var long: Double = -100.93435016893258

    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val bundle = intent.extras
        title = bundle?.getString("title")!!//.toString()
        lat = bundle.getDouble("lat")
        long = bundle.getDouble("long")

        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        createMarker()
    }

    private fun createMarker() {
        val coordinates = LatLng(lat, long)
        val marker = MarkerOptions().position(coordinates).title(title)
        map.addMarker(marker)
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                4000,
                null
        )
    }
}