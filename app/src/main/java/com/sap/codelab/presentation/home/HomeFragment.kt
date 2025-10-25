package com.sap.codelab.presentation.home

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentHomeBinding // We will create this layout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val memoAdapter: MemoAdapter by lazy {
        MemoAdapter(
            onMemoClick = viewModel::onMemoClicked,
            onCheckedChange = viewModel::onMemoCheckedChanged
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        observeUiEvents()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                val isShowAll = viewModel.uiState.value.isShowAllMemosSelected
                menu.findItem(R.id.action_show_all)?.isVisible = !isShowAll
                menu.findItem(R.id.action_show_open)?.isVisible = isShowAll
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_show_all -> {
                        viewModel.onShowAllMemosSelected(); true
                    }
                    R.id.action_show_open -> {
                        viewModel.onShowOpenMemosSelected(); true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = memoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupClickListeners() {
        // binding.fab.setOnClickListener { viewModel.onAddMemoClicked() }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    memoAdapter.submitList(state.memos)
                    activity?.invalidateOptionsMenu()
                    // binding.progressBar.isVisible = state.isLoading
                }
            }
        }
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is HomeContract.HomeUiEvent.NavigateToMemoDetail -> {
                            // TODO: Add navigation logic
                            // findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(event.memoId))
                        }

                        HomeContract.HomeUiEvent.NavigateToCreateMemo -> {
                            // TODO: Add navigation logic
                            // findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateFragment)
                        }
                        is HomeContract.HomeUiEvent.ShowSnackbar -> {
                            Snackbar.make(binding.root, getString(event.messageResId), Snackbar.LENGTH_SHORT).show()
                        }
                        HomeContract.HomeUiEvent.ClearErrorMessage -> {
                            viewModel.onErrorMessageCleared()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}