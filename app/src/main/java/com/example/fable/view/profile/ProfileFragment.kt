package com.example.fable.view.profile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.BuildConfig
import com.example.fable.R
import com.example.fable.databinding.FragmentProfileBinding
import com.example.fable.view.MySnackBar
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionLogout.setOnClickListener {
            viewModel.viewModelScope.launch {
                viewModel.logout()
            }
            MySnackBar.showSnackBar(view, "Logout Successfully")
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(requireActivity(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }, 1000)
        }

        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.apply {
                    tvUserName.text = user.name
                    tvEmail.text = user.email
                    Glide.with(root.context)
                        .load(BuildConfig.BASE_URL_RANDOM_AVATAR)
                        .signature(ObjectKey(user.name))
                        .placeholder(R.drawable.resource_public)
                        .error(R.drawable.resource_public)
                        .into(ivAvatar)
                }
            } else {
                binding.apply {
                    tvUserName.text = getString(R.string.guest)
                    tvEmail.text = getString(R.string.no_email)
                    Glide.with(root.context)
                        .load(BuildConfig.BASE_URL_RANDOM_AVATAR)
                        .signature(ObjectKey(getString(R.string.guest)))
                        .placeholder(R.drawable.resource_public)
                        .error(R.drawable.resource_public)
                        .into(ivAvatar)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}