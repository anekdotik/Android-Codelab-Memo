package com.sap.codelab.presentation.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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

    private val fineLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.onLocationPermissionGranted()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermanentlyDeniedDialog()
                } else {
                    Snackbar.make(binding.root, R.string.permission_dialog_fine_location_denied_message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

    private val backgroundLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onBackgroundPermissionResult(isGranted, hasNotificationPermission())
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onNotificationPermissionResult(isGranted)
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
                        binding.locationText.text = getString(R.string.create_memo_location_coordinates, it.latitude, it.longitude)
                    } ?: run {
                        binding.locationText.text = getString(R.string.create_memo_no_location_selected)
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
                        is CreateMemoContract.UiEvent.ShowPermissionRationale -> showFineLocationPermissionRationaleDialog()
                        is CreateMemoContract.UiEvent.RequestFineLocationPermission -> {
                            fineLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        is CreateMemoContract.UiEvent.ShowBackgroundLocationRationale -> showBackgroundLocationRationale()
                        is CreateMemoContract.UiEvent.RequestBackgroundLocationPermission -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            }
                        }
                        is CreateMemoContract.UiEvent.RequestNotificationPermission -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showFineLocationPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_dialog_fine_location_rationale_title)
            .setMessage(R.string.permission_dialog_fine_location_rationale_message)
            .setPositiveButton(R.string.action_ok) { _, _ ->
                fineLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showBackgroundLocationRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_dialog_background_location_rationale_title)
            .setMessage(R.string.permission_dialog_background_location_rationale_message)
            .setPositiveButton(R.string.action_ok) { _, _ ->
                viewModel.onBackgroundRationaleAccepted()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun showPermanentlyDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_dialog_permanently_denied_title)
            .setMessage(R.string.permission_dialog_permanently_denied_message)
            .setPositiveButton(R.string.action_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun hasBackgroundPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            true
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_create_memo, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_save) {
                    viewModel.onSaveClicked(hasBackgroundPermission(), hasNotificationPermission())
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}