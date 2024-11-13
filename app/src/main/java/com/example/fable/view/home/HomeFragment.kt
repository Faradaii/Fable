package com.example.fable.view.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStories()

        binding.gridStories.rvStories.apply {
            layoutManager = GridLayoutManager(context, 2)
        }

        binding.gridStories.swipeRefresh.setOnRefreshListener {
            getStories()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.gridStories.swipeRefresh.isRefreshing = false
            }, 2000)
        }
    }

    private fun setAdapter(stories: List<Story>){
        val adapter = StoryItemAdapter()
        adapter.submitList(stories)
        binding.gridStories.rvStories.adapter = adapter

    }

    fun getStories() {
        viewModel.getAllStories().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showState(isLoading = true)
                    }
                    is Result.Error -> {
                        showState(isError = true)
                    }
                    is Result.Success -> {
                        if (result.data.listStory.isEmpty()) {
                            showState(isEmpty = true)
                        } else {
                            showState(isShowStories = true)
                        }
                        setAdapter(result.data.listStory)
                    }
                }
            }
        }
    }

    private fun showState(
        isShowStories: Boolean = false,
        isEmpty: Boolean = false,
        isError: Boolean = false,
        isLoading: Boolean = false,
    ) {
        binding.gridStories.apply {
            rvStories.visibility = if (isShowStories) View.VISIBLE else View.GONE
            stateEmpty.emptyStateContainer.visibility = if (isEmpty) View.VISIBLE else View.GONE
            stateError.errorStateContainer.visibility = if (isError) View.VISIBLE else View.GONE
            stateLoading.loadingStateContainer.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}