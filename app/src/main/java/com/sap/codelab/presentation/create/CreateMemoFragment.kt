package com.sap.codelab.presentation.create

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.sap.codelab.R
import com.sap.codelab.common.utils.viewBinding
import com.sap.codelab.databinding.FragmentCreateMemoBinding
import com.sap.codelab.presentation.location.SelectLocationFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateMemoFragment : Fragment(R.layout.fragment_create_memo) {

    private val binding by viewBinding(FragmentCreateMemoBinding::bind)
    private val viewModel: CreateMemoViewModel by viewModels()

    private val locationPermissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.onLocationPermissionGranted()
            } else {
                Snackbar.make(binding.root, R.string.location_permission_denied_message, Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupListeners()
        observeUiState()
        observeUiEvents()
    }

    private fun setupListeners() {
        binding.memoTitle.doAfterTextChanged { viewModel.onTitleChanged(it.toString()) }
        binding.memoDescription.doAfterTextChanged { viewModel.onDescriptionChanged(it.toString()) }

        binding.buttonAddLocation.setOnClickListener {
            val hasPermission = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val shouldShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

            viewModel.onAddLocationClicked(hasPermission, shouldShowRationale)
        }

        setFragmentResultListener(SelectLocationFragment.REQUEST_KEY_LOCATION) { _, bundle ->
            val location = bundle.getParcelable<LatLng>(SelectLocationFragment.BUNDLE_KEY_LOCATION)
            location?.let { viewModel.onLocationSelected(it) }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.memoTitleContainer.error = state.titleError?.let { getString(it) }
                    binding.memoDescriptionContainer.error = state.descriptionError?.let { getString(it) }

                    state.selectedLocation?.let {
                        binding.locationText.text = getString(R.string.location_coordinates, it.latitude, it.longitude)
                    } ?: run {
                        binding.locationText.text = getString(R.string.no_location_selected)
                    }
                }
            }
        }
    }

    private fun observeUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is CreateMemoContract.UiEvent.NavigateBack -> findNavController().popBackStack()
                        is CreateMemoContract.UiEvent.ShowSnackbar -> {
                            Snackbar.make(binding.root, getString(event.messageResId), Snackbar.LENGTH_SHORT).show()
                        }
                        is CreateMemoContract.UiEvent.NavigateToSelectLocation -> {
                            val action = CreateMemoFragmentDirections.actionNavCreateMemoFragmentToNavSelectLocationFragment()
                            findNavController().navigate(action)
                        }
                        is CreateMemoContract.UiEvent.ShowPermissionRationale -> showPermissionRationaleDialog()
                        is CreateMemoContract.UiEvent.RequestFineLocationPermission -> {
                            locationPermissionRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.location_permission_rationale_title)
            .setMessage(R.string.location_permission_rationale_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                locationPermissionRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
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
}