package com.example.fable.view.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.BuildConfig
import com.example.fable.R
import com.example.fable.databinding.FragmentDetailBinding
import com.example.fable.view.ViewModelFactory
import com.example.fable.data.Result
import com.example.fable.util.Util

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: DetailViewModel by viewModels {
            factory
        }

        viewModel.load(ARG_STORY_ID).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        binding.apply {
                            tvDetailName.text = result.data.story!!.name
                            tvDetailDescription.text = result.data.story.description
                            tvDate.text = Util.formatDate(result.data.story.createdAt.toString())

                            Glide.with(root.context)
                                .load(result.data.story.photoUrl)
                                .placeholder(R.drawable.ic_image_24).fitCenter()
                                .error(R.drawable.ic_image_24).fitCenter()
                                .into(ivDetailPhoto)

                            Glide.with(root.context)
                                .load(BuildConfig.BASE_URL_RANDOM_AVATAR)
                                .signature(ObjectKey(result.data.story.name.toString()))
                                .placeholder(R.drawable.resource_public)
                                .error(R.drawable.resource_public)
                                .into(ivAvatar)

                            close.setOnClickListener{
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            }
                        }
                        Toast.makeText(context, result.data.story!!.name, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private var ARG_STORY_ID = "eventId"

        fun newInstance(storyId: String): DetailFragment {
            ARG_STORY_ID = storyId
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString("EXTRA_ID", ARG_STORY_ID)
            fragment.arguments = args
            return fragment
        }
    }
}