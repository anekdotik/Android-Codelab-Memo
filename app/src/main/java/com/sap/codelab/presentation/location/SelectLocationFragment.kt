package com.sap.codelab.presentation.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sap.codelab.R
import com.sap.codelab.common.utils.viewBinding
import com.sap.codelab.databinding.FragmentSelectLocationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectLocationFragment : Fragment(R.layout.fragment_select_location), OnMapReadyCallback {

    private val binding by viewBinding(FragmentSelectLocationBinding::bind)
    private lateinit var map: GoogleMap
    private var selectedLocation: LatLng? = null

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                enableMyLocationOnMap()
            } else {
                moveCameraToDefaultLocation()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.fabConfirmLocation.setOnClickListener {
            selectedLocation?.let { location ->
                setFragmentResult(REQUEST_KEY_LOCATION, bundleOf(BUNDLE_KEY_LOCATION to location))
                findNavController().popBackStack()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        map.setOnMapClickListener { latLng ->
            map.clear()
            selectedLocation = latLng
            map.addMarker(MarkerOptions().position(latLng))
        }

        checkPermissionAndSetupMap()
    }

    private fun checkPermissionAndSetupMap() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocationOnMap()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocationOnMap() {
        map.isMyLocationEnabled = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
            } else {
                moveCameraToDefaultLocation()
            }
        }.addOnFailureListener {
            moveCameraToDefaultLocation()
        }
    }

    private fun moveCameraToDefaultLocation() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LAT_LNG, DEFAULT_ZOOM_LEVEL))
    }

    companion object {
        const val REQUEST_KEY_LOCATION = "location_request"
        const val BUNDLE_KEY_LOCATION = "selected_location"
        private val DEFAULT_LAT_LNG = LatLng(49.4, 8.6)
        private const val DEFAULT_ZOOM_LEVEL = 5f
    }
}