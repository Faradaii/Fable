package com.example.fable.view.explore

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.R
import com.example.fable.data.Result
import com.example.fable.databinding.FragmentExploreBinding
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.component.bottomsheet.PermissionBottomSheet
import com.example.fable.view.component.snackbar.MySnackBar
import com.example.fable.view.detail.DetailActivity
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

class ExploreFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ExploreViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))
            .get(ExploreViewModel::class.java)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.topAppBar.title = "Explore"
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

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            showPermissionRequest()
        }
    }

    private fun addStoriesToMap() {
        viewModel.getStoriesWithLocation().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        Toast.makeText(context, "Getting worldwide stories...", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Result.Error -> {
                        MySnackBar.showSnackBar(binding.root, result.error)
                    }

                    is Result.Success -> {
                        MySnackBar.showSnackBar(binding.root, "Successfully get worldwide stories")

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
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.md_theme_primary
                                        )
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

                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                            boundsBuilder.include(latLng)
                        }

                        mMap.setOnMarkerClickListener { marker ->
                            if (marker.tag != null) {
                                val intent = Intent(requireContext(), DetailActivity::class.java)
                                intent.putExtra(DetailActivity.EXTRA_ID, marker.tag.toString())
                                startActivity(
                                    intent,
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        requireActivity()
                                    )
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
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                MySnackBar.showSnackBar(binding.root, "Style parsing failed!")
            }
        } catch (exception: Resources.NotFoundException) {
            MySnackBar.showSnackBar(binding.root, "Cannot find style map!")
        }
    }

    private fun showPermissionRequest() {
        val permissionFragment = PermissionBottomSheet()
        permissionFragment.show(childFragmentManager, "PermissionBottomSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ExploreFragment = ExploreFragment()
    }
}
