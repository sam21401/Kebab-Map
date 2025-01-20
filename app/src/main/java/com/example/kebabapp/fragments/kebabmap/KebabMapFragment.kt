package com.example.kebabapp.fragments.kebabmap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kebabapp.KebabDetailPageViewModel
import com.example.kebabapp.KebabPlaceViewModel
import com.example.kebabapp.KebabPlaces
import com.example.kebabapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class KebabMapFragment : Fragment() {
    private lateinit var kebabDetailPageViewModel: KebabDetailPageViewModel
    private val callback =
        OnMapReadyCallback { googleMap ->
            val legnicaCity = LatLng(51.2070, 16.1753)
            kebabDetailPageViewModel = ViewModelProvider(requireActivity()).get(KebabDetailPageViewModel::class.java)
            val kebabViewModel = ViewModelProvider(requireActivity()).get(KebabPlaceViewModel::class.java)
            val listOfMarkers = createListOfMarkers(kebabViewModel.getKebabPlaces())
            for (e in listOfMarkers) {
                val marker = googleMap.addMarker(e.value)
                marker?.let {
                    googleMap.setOnMarkerClickListener { clickedMarker ->
                        // This will trigger the info window popup on marker click
                        clickedMarker.showInfoWindow()
                        true
                    }
                }
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(legnicaCity, 14f))
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_basic))
            googleMap.setOnInfoWindowClickListener { marker ->
                val kebabId = listOfMarkers.entries.find {it.value.title.toString() == marker.title.toString() }?.key
                Log.i("InfoWindow", kebabId.toString())
                if (kebabId != null) {
                    kebabDetailPageViewModel.setKebabId(kebabId)
                    val navController = this.findNavController()
                    navController.navigate(R.id.action_navigation_map_to_navigation_kebab_detail_page)
                }
            }
        }

    private fun getBitmapFromDrawable(resId: Int): Bitmap? {
        var bitmap: Bitmap? = null
        val drawable = ResourcesCompat.getDrawable(resources, resId, null)
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_kebab_map, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        kebabDetailPageViewModel = ViewModelProvider(requireActivity()).get(KebabDetailPageViewModel::class.java)
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun createListOfMarkers(kebabPlaces: KebabPlaces): MutableMap<Int,MarkerOptions> {
        val listOfMarkers: MutableMap<Int,MarkerOptions> = mutableMapOf()
        for (item in kebabPlaces) {
            val markerOptions = MarkerOptions()
            markerOptions.title(item.name)
            markerOptions.snippet(item.address)
            markerOptions.position(LatLng(item.latitude.toDouble(), item.longitude.toDouble()))
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.ic_kebab_marker)))
            listOfMarkers.put(item.id,markerOptions)
        }
        return listOfMarkers
    }
}
