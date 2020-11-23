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

    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val bundle = intent.extras
        val title = bundle?.getString("title")
        val lat = bundle?.getBoolean("lat")
        val long = bundle?.getBoolean("long")

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
        val coordinates = LatLng(21.15794952216306, -100.93435016893258)
        val marker = MarkerOptions().position(coordinates).title("Mi playa favorita")
        map.addMarker(marker)
        map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                4000,
                null
        )
    }
}