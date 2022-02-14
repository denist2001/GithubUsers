package com.denis.github_users.presentation.itemsList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.denis.github_users.R
import com.denis.github_users.databinding.ItemsFragmentBinding
import com.denis.github_users.utils.throttleFirst
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ItemsFragment : Fragment(R.layout.items_fragment) {

    @Inject
    lateinit var itemsListAdapter: ItemsListAdapter

    private var _binding: ItemsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val viewModel by viewModels<ItemsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ItemsFragmentBinding.bind(view)
        setupRecyclerView()
        startObserving()
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            callbackFlow {
                itemsListAdapter.setClickCollector { direction ->
                    trySend(direction)
                }
                awaitClose { itemsListAdapter.setClickCollector(null) }
            }.throttleFirst(1000).collectLatest { direction ->
                findNavController().navigate(direction)
            }
        }
        linearLayoutManager = LinearLayoutManager(context)
        if (binding.itemsListRv.adapter == null) {
            with(binding.itemsListRv) {
                adapter = itemsListAdapter
                layoutManager = linearLayoutManager
            }
        }
        lifecycleScope.launch {
            itemsListAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressPb.visibility = when (loadState.refresh) {
                    is LoadState.Loading -> View.VISIBLE
                    is LoadState.NotLoading -> View.INVISIBLE
                    is LoadState.Error -> {
                        val errorMessage = (loadState.refresh as LoadState.Error).error.message
                        if (!errorMessage.isNullOrBlank()) {
                            Snackbar.make(binding.root, errorMessage, LENGTH_LONG).show()
                        }
                        View.INVISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.itemsListRv.adapter = null
        _binding = null
    }

    private fun startObserving() {
        lifecycleScope.launchWhenCreated {
            viewModel.itemsFlow.collectLatest { presentationEntity ->
                itemsListAdapter.submitData(presentationEntity)
            }
        }
    }
}