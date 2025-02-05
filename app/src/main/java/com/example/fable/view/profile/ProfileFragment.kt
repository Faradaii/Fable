package com.example.fable.view.profile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.BuildConfig
import com.example.fable.R
import com.example.fable.databinding.FragmentProfileBinding
import com.example.fable.util.Util
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.component.myImageView.ImageView.loadImage
import com.example.fable.view.component.snackbar.MySnackBar
import com.example.fable.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel =
            ViewModelFactory.getInstance(requireActivity()).create(ProfileViewModel::class.java)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogout.setOnClickListener {
            viewModel.viewModelScope.launch {
                viewModel.logout()
            }
            MySnackBar.showSnackBar(view, getString(R.string.logout_successfully))
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(requireActivity(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }, Util.ONE_SECOND)
        }

        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.apply {
                    tvUserName.text = user.name
                    tvEmail.text = user.email
                    ivAvatar.loadImage(
                        root.context,
                        BuildConfig.BASE_URL_RANDOM_AVATAR,
                        signature = ObjectKey(user.name),
                    )
                }
            } else {
                binding.apply {
                    tvUserName.text = getString(R.string.guest)
                    tvEmail.text = getString(R.string.no_email)
                    ivAvatar.loadImage(
                        root.context,
                        BuildConfig.BASE_URL_RANDOM_AVATAR,
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}