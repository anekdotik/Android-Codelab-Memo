package com.sap.codelab.data.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.sap.codelab.common.utils.NotificationManager
import com.sap.codelab.di.ApplicationScope
import com.sap.codelab.domain.repository.GeofenceRepository
import com.sap.codelab.domain.usecase.GetMemoByIdUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var getMemoByIdUseCase: GetMemoByIdUseCase
    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var geofenceRepository: GeofenceRepository

    @Inject
    @ApplicationScope
    lateinit var externalScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            val errorCode = geofencingEvent?.errorCode ?: -1
            Log.e(TAG, "Error receiving geofence event. Error code: $errorCode")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            geofencingEvent.triggeringGeofences?.forEach { geofence ->
                val memoId = geofence.requestId.toLongOrNull()
                if (memoId == null) {
                    Log.e(TAG, "Invalid memo ID found in geofence request ID: ${geofence.requestId}")
                    return@forEach
                }
                Log.d(TAG, "Geofence ENTER event triggered for memo ID: $memoId")

                externalScope.launch {
                    try {
                        val memo = getMemoByIdUseCase(memoId)
                        memo?.let {
                            this@GeofenceBroadcastReceiver.notificationManager.showNotification(it)

                            // geofenceRepository.removeGeofence(it.id)
                            // Log.d(TAG, "One-time geofence removed for memo ID: ${it.id}")
                        } ?: Log.w(TAG, "Memo with ID $memoId not found when processing geofence event.")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing geofence event for memo ID $memoId", e)
                    }
                }
            }
        } else {
            Log.d(TAG, "Received geofence transition: ${geofencingEvent.geofenceTransition}. Ignoring as only ENTER is relevant.")
        }
    }
    companion object {
        private const val TAG = "GeofenceReceiver"
    }
}