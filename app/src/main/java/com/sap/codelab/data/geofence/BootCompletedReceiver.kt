package com.sap.codelab.data.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sap.codelab.di.ApplicationScope
import com.sap.codelab.domain.repository.GeofenceRepository
import com.sap.codelab.domain.usecase.GetOpenMemosUseCase
import com.sap.codelab.domain.usecase.SaveMemoUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getOpenMemosUseCase: GetOpenMemosUseCase

    @Inject
    lateinit var geofenceRepository: GeofenceRepository

    @Inject
    @ApplicationScope
    lateinit var externalScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        val pendingResult: PendingResult = goAsync()

        externalScope.launch {
            try {
                val openMemos = getOpenMemosUseCase().first()

                if (openMemos.isEmpty()) {
                    Log.i(TAG, "No open memos with location reminders to re-register.")
                    return@launch
                }

                Log.d(TAG, "Found ${openMemos.size} open memos to process for geofence re-registration.")

                for (memo in openMemos) {
                    val result = geofenceRepository.addGeofence(memo, SaveMemoUseCase.GEOFENCE_RADIUS_IN_METERS)
                    if (result.isSuccess) {
                        Log.i(TAG, "Successfully re-registered geofence for memo ID: ${memo.id}")
                    } else {
                        Log.e(TAG, "Failed to re-register geofence for memo ID: ${memo.id}. Error: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "An error occurred during geofence re-registration.", e)
            } finally {
                pendingResult.finish()
                Log.i(TAG, "Geofence re-registration process finished.")
            }
        }
    }

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }
}