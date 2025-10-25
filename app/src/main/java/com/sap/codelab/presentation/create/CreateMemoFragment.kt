package com.sap.codelab.presentation.create

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentCreateMemoBinding
import com.sap.codelab.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateMemoFragment : Fragment(R.layout.fragment_create_memo) {

    private val binding by viewBinding(FragmentCreateMemoBinding::bind)
    private val viewModel: CreateMemoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupInputListeners()
        observeUiState()
        observeUiEvents()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_create_memo, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_save -> {
                        viewModel.onSaveClicked()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupInputListeners() = with(binding) {
        binding.memoTitle.doAfterTextChanged {
            viewModel.onTitleChanged(it.toString())
        }
        binding.memoDescription.doAfterTextChanged {
            viewModel.onDescriptionChanged(it.toString())
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.memoTitleContainer.error = state.titleError?.let { getString(it) }
                    binding.memoDescriptionContainer.error = state.descriptionError?.let { getString(it) }
                }
            }
        }
    }

    private fun observeUiEvents() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is CreateMemoContract.UiEvent.NavigateBack -> {
                            findNavController().popBackStack()
                        }
                        is CreateMemoContract.UiEvent.ShowSnackbar -> {
                            Snackbar.make(binding.root, getString(event.messageResId), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}