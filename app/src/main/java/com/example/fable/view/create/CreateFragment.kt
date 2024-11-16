package com.example.fable.view.create

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.fable.R
import com.example.fable.data.Result
import com.example.fable.databinding.FragmentCreateBinding
import com.example.fable.util.Util
import com.example.fable.util.Util.reduceFileImage
import com.example.fable.util.Util.uriToFile
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.component.bottomsheet.PermissionBottomSheet
import com.example.fable.view.component.snackbar.MySnackBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var location: Location


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel =
            ViewModelFactory.getInstance(requireActivity()).create(CreateViewModel::class.java)
        viewModel.currentImageUri?.let { showImage(it) }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            addPhotoButton.setOnClickListener{ startAddPhoto() }
            buttonAdd.setOnClickListener{ uploadStory() }
            swLocation.setOnCheckedChangeListener { _, isChecked -> if (isChecked) getMyLocation() }
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedImageUri = result.uriContent
            viewModel.currentImageUri = croppedImageUri
            showImage(croppedImageUri!!)
        } else {
            showToast(getString(R.string.adding_image_canceled))
        }
    }

    private fun startAddPhoto() {
        cropImage.launch(
            input = CropImageContractOptions(uri = null, cropImageOptions = CropImageOptions(
                imageSourceIncludeGallery = true,
                imageSourceIncludeCamera = true,
                allowRotation = true,
                allowFlipping = true,
                allowCounterRotation = true
            ))
        )
    }

    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
    }

    private fun uploadStory() {
        if (viewModel.currentImageUri == null) {
            showToast(getString(R.string.please_add_a_photo_first))
            return
        }
        if (binding.edAddDescription.text.isNullOrEmpty()) {
            showToast(getString(R.string.please_add_a_description))
            return
        }

        val imageFile = uriToFile(viewModel.currentImageUri!!, requireActivity()).reduceFileImage()
        val desc = binding.edAddDescription.text.toString()

        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val descRequestBody = desc.toRequestBody("text/plain".toMediaType())
        var latitudeRequestBody: RequestBody? = null
        var longitudeRequestBody: RequestBody? = null

        if (binding.swLocation.isChecked) {
            latitudeRequestBody =
                location.latitude.toString().toRequestBody("text/plain".toMediaType())
            longitudeRequestBody =
                location.longitude.toString().toRequestBody("text/plain".toMediaType())
        }

        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )


        viewModel.addStory(
            multipartBody,
            descRequestBody,
            latitudeRequestBody,
            longitudeRequestBody
        ).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showToast(getString(R.string.trying_to_share_your_story))
                    }
                    is Result.Error -> {
                        showToast(result.error)
                    }
                    is Result.Success -> {
                        MySnackBar.showSnackBar(
                            binding.root,
                            getString(R.string.your_story_has_been_shared)
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            requireActivity().setResult(Activity.RESULT_OK)
                            requireActivity().finish()
                        }, Util.ONE_SECOND)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        this.location = location
                        MySnackBar.showSnackBar(
                            binding.root,
                            getString(R.string.your_story_can_viewed_in_explore_page)
                        )
                        binding.swLocation.isChecked = true
                    } else {
                        MySnackBar.showSnackBar(binding.root, getString(R.string.please_enable_gps))
                        binding.swLocation.isChecked = false
                        requestSingleLocationUpdate()
                    }
                }
                .addOnFailureListener {
                    MySnackBar.showSnackBar(
                        binding.root,
                        getString(R.string.failed_to_get_location)
                    )
                    binding.swLocation.isChecked = false
                }
        } else {
            showPermissionRequest()
            binding.swLocation.isChecked = false
        }
    }

    private fun requestSingleLocationUpdate() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, Util.ONE_SECOND
        ).setMaxUpdates(1).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.lastLocation != null) {
                    location = locationResult.lastLocation!!
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            showPermissionRequest()
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
        fun newInstance(): CreateFragment = CreateFragment()
    }
}