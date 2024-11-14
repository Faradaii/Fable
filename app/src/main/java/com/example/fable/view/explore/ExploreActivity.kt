package com.example.fable.view.explore

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.R
import com.example.fable.data.Result
import com.example.fable.databinding.ActivityExploreBinding
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.detail.DetailActivity
import com.example.fable.view.snackbar.MySnackBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation

class ExploreActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityExploreBinding
    private lateinit var viewModel: ExploreViewModel

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelFactory.getInstance(this).create(ExploreViewModel::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        setMapStyle()
        getMyLocation()
        addStoriesToMap()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun addStoriesToMap() {
        viewModel.getStoriesWithLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        Toast.makeText(this, "Getting worldwide stories...", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Result.Error -> {
                        MySnackBar.showSnackBar(
                            binding.root,
                            result.error
                        )
                    }

                    is Result.Success -> {
                        MySnackBar.showSnackBar(
                            binding.root,
                            "Successfully get worldwide stories"
                        )

                        result.data.listStory.forEach { data ->
                            val latLng = LatLng(data.lat!!, data.lon!!)

                            Glide.with(binding.root.context)
                                .asBitmap()
                                .load(data.photoUrl)
                                .apply(RequestOptions().override(200, 200))
                                .signature(ObjectKey(data.id!!))
                                .circleCrop()
                                .transform(
                                    CenterCrop(),
                                    CropCircleWithBorderTransformation(
                                        10,
                                        ContextCompat.getColor(this, R.color.md_theme_primary)
                                    )
                                )
                                .placeholder(R.drawable.icon_marker_custom_24)
                                .error(R.drawable.icon_marker_custom_24)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?,
                                    ) {
                                        val bitmapDescriptor =
                                            BitmapDescriptorFactory.fromBitmap(resource)
                                        val marker = mMap.addMarker(
                                            MarkerOptions()
                                                .position(latLng)
                                                .title(data.name)
                                                .snippet(data.description)
                                                .icon(bitmapDescriptor)
                                        )
                                        marker?.tag = data.id
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }
                                }
                                )
                            boundsBuilder.include(latLng)
                        }

                        mMap.setOnMarkerClickListener { marker ->
                            if (marker.tag != null) {
                                val intent = Intent(
                                    this@ExploreActivity,
                                    DetailActivity::class.java
                                )
                                intent.putExtra(DetailActivity.EXTRA_ID, marker.tag.toString())
                                this.startActivity(
                                    intent,
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                                        .toBundle()
                                )
                            }
                            true
                        }

                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                300
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                MySnackBar.showSnackBar(binding.root, "Style parsing failed!")
            }
        } catch (exception: Resources.NotFoundException) {
            MySnackBar.showSnackBar(binding.root, "Cannot find style map!")

        }
    }
}