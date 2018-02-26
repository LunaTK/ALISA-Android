package com.lunatk.alisa.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.lunatk.mybluetooth.R
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.lunatk.alisa.util.Utils
import android.widget.RelativeLayout
import com.google.android.gms.maps.model.CameraPosition


/**
 * Created by LunaTK on 2018. 2. 26..
 */
class TrackingActivity: AppCompatActivity(), OnMapReadyCallback {

    var gMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)


        val fragmentManager = fragmentManager
        val mapFragment = fragmentManager
                .findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(map: GoogleMap?) {

        gMap = map
        map?.let{
            val SEOUL = LatLng(37.56, 126.97)

            val markerOptions = MarkerOptions()
            markerOptions.position(SEOUL)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeBitmap(this, R.drawable.ic_marker, 200,230)))
            map.addMarker(markerOptions)
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false
            with(map.uiSettings){
                isCompassEnabled = true
                isMapToolbarEnabled = true
            }

            map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL))
            map.animateCamera(CameraUpdateFactory.zoomTo(18f))
        }
    }

    fun onMyLocation(view: View){
        getMyLocation()
    }

    fun onCarLocation(view: View){
        var carLocation = CameraPosition.Builder().bearing(0f).tilt(0f).zoom(18f).target(LatLng(37.56, 126.97)).build()
        gMap?.animateCamera(CameraUpdateFactory.newCameraPosition(carLocation), 1000, null)
    }

    fun onRefresh(view: View){
        var currentPlace = CameraPosition.Builder().bearing(0f).tilt(0f).zoom(18f).target(gMap?.cameraPosition?.target).build()
        gMap?.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 600, null)
    }


    private fun getMyLocation() {
        var latLng = LatLng(gMap?.myLocation?.latitude!!, gMap?.myLocation?.longitude!!);
        var cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f);
        gMap?.animateCamera(cameraUpdate);
    }
}