package com.example.fable.view.component.bottomsheet

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fable.R
import com.example.fable.databinding.FragmentPermissionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PermissionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentPermissionBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentPermissionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btAskPermission.setOnClickListener {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    showToast(getString(R.string.now_you_re_able_to_use_this_feature))
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getString(R.string.now_you_re_able_to_use_this_feature)
                }

                else -> {
                    showToast(getString(R.string.oops_you_re_not_fully_able_to_use_this_feature))
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
}