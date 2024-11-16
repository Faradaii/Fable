package com.example.fable.view.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fable.databinding.FragmentHomeBinding
import com.example.fable.util.Util
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.component.adapter.LoadingAdapter
import com.example.fable.view.component.adapter.StoryItemAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private val storyAdapter by lazy { StoryItemAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel =
            ViewModelFactory.getInstance(requireActivity()).create(HomeViewModel::class.java)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        getStories()

        binding.gridStories.swipeRefresh.setOnRefreshListener {
            storyAdapter.refresh()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.gridStories.swipeRefresh.isRefreshing = false
            }, Util.TWO_SECONDS)
        }
    }

    private fun setupRecyclerView() {
        binding.gridStories.rvStories.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingAdapter {
                    storyAdapter.retry()
                }
            )
        }

        storyAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> showState(isLoading = true)
                is LoadState.NotLoading -> showState(isShowStories = true)
                is LoadState.Error -> showState(isError = true)
            }
        }
    }

    fun getStories() {
        viewModel.getAllStories().observe(viewLifecycleOwner) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
        }
    }

    private fun showState(
        isShowStories: Boolean = false,
        isEmpty: Boolean = false,
        isError: Boolean = false,
        isLoading: Boolean = false,
        errorMessage: String = "",
    ) {
        binding.gridStories.apply {
            rvStories.visibility = if (isShowStories) View.VISIBLE else View.GONE
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
}