package com.example.fable.view.create

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.fable.data.Result
import com.example.fable.databinding.FragmentCreateBinding
import com.example.fable.util.Util.reduceFileImage
import com.example.fable.util.Util.uriToFile
import com.example.fable.view.MySnackBar
import com.example.fable.view.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.currentImageUri?.let { showImage(it) }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            addPhotoButton.setOnClickListener{ startAddPhoto() }
            buttonAdd.setOnClickListener{ uploadStory() }
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
        val requestBody = desc.toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )


        viewModel.addStory(multipartBody, requestBody).observe(viewLifecycleOwner) { result ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): CreateFragment = CreateFragment()
    }
}