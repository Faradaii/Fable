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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.fable.data.Result
import com.example.fable.databinding.FragmentCreateBinding
import com.example.fable.util.Util.reduceFileImage
import com.example.fable.util.Util.uriToFile
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.snackbar.MySnackBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
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
            val exception = result.error
            showToast(exception.toString())
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
            showToast("Please add a photo first")
            return
        }
        if (binding.edAddDescription.text.isNullOrEmpty()) {
            showToast("Please add a description")
            return
        }

        val imageFile = uriToFile(viewModel.currentImageUri!!, requireActivity()).reduceFileImage()
        val desc = binding.edAddDescription.text.toString()

        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val descRequestBody = desc.toRequestBody("text/plain".toMediaType())
        val latitudeRequestBody =
            location.latitude.toString().toRequestBody("text/plain".toMediaType())
        val longitudeRequestBody =
            location.longitude.toString().toRequestBody("text/plain".toMediaType())
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
                        showToast("Trying to share your story...")
                    }
                    is Result.Error -> {
                        showToast(result.error)
                    }
                    is Result.Success -> {
                        MySnackBar.showSnackBar(binding.root, "Your story has been shared!")
                        Handler(Looper.getMainLooper()).postDelayed({
                            requireActivity().setResult(Activity.RESULT_OK)
                            requireActivity().finish()
                        }, 1000)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLocation()
                }

                else -> {
                    //TODO: Show an explanation to the user
                }
            }
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
                        Snackbar.make(binding.root, "Location is found", Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Location is not found, Please enable location!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        binding.swLocation.isChecked = false
                        requestSingleLocationUpdate()
                    }
                }
                .addOnFailureListener {
                    Snackbar.make(binding.root, "Failed to get location", Snackbar.LENGTH_SHORT)
                        .show()
                    binding.swLocation.isChecked = false
                }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun requestSingleLocationUpdate() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
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
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): CreateFragment = CreateFragment()
    }
}