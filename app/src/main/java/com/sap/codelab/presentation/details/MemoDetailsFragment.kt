package com.sap.codelab.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentMemoDetailsBinding
import com.sap.codelab.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MemoDetailsFragment : Fragment(R.layout.fragment_memo_details) {

    private val binding by viewBinding(FragmentMemoDetailsBinding::bind)
    private val viewModel: MemoDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    binding.errorText.isVisible = state.error != null
                    state.error?.let { binding.errorText.text = getString(it) }

                    state.memo?.let {
                        binding.memoTitle.text = it.title
                        binding.memoDescription.text = it.description
                    }
                }
            }
        }
    }
}