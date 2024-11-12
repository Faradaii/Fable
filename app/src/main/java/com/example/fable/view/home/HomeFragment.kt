package com.example.fable.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fable.data.Result
import com.example.fable.data.local.entity.Story
import com.example.fable.databinding.FragmentHomeBinding
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.adapter.StoryItemAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: HomeViewModel by viewModels {
            factory
        }

        viewModel.getAllStories().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        setAdapter(result.data.listStory)
                    }
                }
            }
        }

        binding.gridStories.rvStories.apply {
            layoutManager = GridLayoutManager(context, 2)
        }
    }


    private fun setAdapter(stories: List<Story>){
        val adapter = StoryItemAdapter()
        adapter.submitList(stories)
        binding.gridStories.rvStories.adapter = adapter

    }

//    private fun showEmptyState(isEmpty: Boolean, section: Enum<Section>) {
//        if (Section.UPCOMING == section) {
//            binding.listEventsCardHorizontal.apply {
//                rvEventsCard.visibility = if (isEmpty) View.GONE else View.VISIBLE
//                emptyStateContainer.emptyStateContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
//            }
//        } else {
//            binding.listEventsCardVertical.apply {
//                rvEventsCard.visibility = if (isEmpty) View.GONE else View.VISIBLE
//                emptyStateContainer.emptyStateContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
//            }
//        }
//    }
//
//    private fun showErrorState(isError: Boolean, section: Enum<Section>) {
//        if (Section.UPCOMING == section) {
//            binding.listEventsCardHorizontal.apply {
//                errorStateContainer.errorStateContainer.visibility = if (isError) View.VISIBLE else View.GONE
//                if (isError) {
//                    rvEventsCard.visibility = View.GONE
//                    emptyStateContainer.emptyStateContainer.visibility = View.GONE
//                }
//            }
//        } else {
//            binding.listEventsCardVertical.apply {
//                errorStateContainer.errorStateContainer.visibility = if (isError) View.VISIBLE else View.GONE
//                if (isError) {
//                    rvEventsCard.visibility = View.GONE
//                    emptyStateContainer.emptyStateContainer.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    private fun showLoading(isLoading: Boolean, section: Enum<Section>) {
//        if (Section.UPCOMING == section) {
//            binding.listEventsCardHorizontal.apply {
//                progressBar.loadingStateContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
//                if (isLoading) {
//                    rvEventsCard.visibility = View.GONE
//                    errorStateContainer.errorStateContainer.visibility = View.GONE
//                    emptyStateContainer.emptyStateContainer.visibility = View.GONE
//                } else {
//                    rvEventsCard.visibility = View.VISIBLE
//                }
//            }
//        } else {
//            binding.listEventsCardVertical.apply {
//                progressBar.loadingStateContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
//                if (isLoading) {
//                    rvEventsCard.visibility = View.GONE
//                    errorStateContainer.errorStateContainer.visibility = View.GONE
//                    emptyStateContainer.emptyStateContainer.visibility = View.GONE
//                } else {
//                    rvEventsCard.visibility = View.VISIBLE
//                }
//            }
//        }
//    }

//    enum class Section {UPCOMING, FINISHED}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}