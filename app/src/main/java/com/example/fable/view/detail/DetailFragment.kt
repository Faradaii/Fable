package com.example.fable.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.BuildConfig
import com.example.fable.R
import com.example.fable.data.Result
import com.example.fable.databinding.FragmentDetailBinding
import com.example.fable.util.Util
import com.example.fable.view.ViewModelFactory

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
                        showState(isLoading = true)
                    }
                    is Result.Error -> {
                        showState(isError = true, errorMessage = result.error)
                    }
                    is Result.Success -> {
                        if (result.data.story == null) {
                            showState(isEmpty = true)
                        } else {
                            showState(isShowStory = true)
                            binding.apply {
                                tvDetailName.text = result.data.story.name
                                tvDetailDescription.text = result.data.story.description
                                tvDate.text =
                                    Util.formatDate(result.data.story.createdAt.toString())

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

                            }
                        }
                        binding.close.setOnClickListener {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            }
        }
    }

    private fun showState(
        isShowStory: Boolean = false,
        isEmpty: Boolean = false,
        isError: Boolean = false,
        isLoading: Boolean = false,
        errorMessage: String = "",
    ) {
        binding.apply {
            constraintDetail.visibility = if (isShowStory) View.VISIBLE else View.GONE
            stateEmpty.emptyStateContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
            stateError.errorStateContainer.visibility = if (isError) View.VISIBLE else View.GONE
            stateError.tvHeadError.text = if (isError) errorMessage else ""
            stateLoading.loadingStateContainer.visibility =
                if (isLoading) View.VISIBLE else View.GONE
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