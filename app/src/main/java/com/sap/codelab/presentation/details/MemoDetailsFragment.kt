package com.sap.codelab.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sap.codelab.R
import com.sap.codelab.common.utils.launchAndCollectIn
import com.sap.codelab.common.utils.viewBinding
import com.sap.codelab.databinding.FragmentMemoDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoDetailsFragment : Fragment(R.layout.fragment_memo_details) {

    private val binding by viewBinding(FragmentMemoDetailsBinding::bind)
    private val viewModel: MemoDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
    }

    private fun observeUiState() {
        viewModel.uiState.launchAndCollectIn(viewLifecycleOwner) { state ->
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