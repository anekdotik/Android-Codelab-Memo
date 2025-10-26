package com.sap.codelab.data.repository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.sap.codelab.data.geofence.GeofenceBroadcastReceiver
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.GeofenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GeofenceRepository {

    private val geofencingClient by lazy { LocationServices.getGeofencingClient(context) }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    override suspend fun addGeofence(memo: Memo): Result<Unit> = runCatching {
        if (memo.reminderLatitude == 0.0 && memo.reminderLongitude == 0.0) {
            Log.w(TAG, "Skipping geofence for memo ${memo.id}: no valid reminder location set.")
            return Result.success(Unit)
        }

        val geofence = Geofence.Builder()
            .setRequestId(memo.id.toString())
            .setCircularRegion(
                memo.reminderLatitude,
                memo.reminderLongitude,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).await()
        Log.i(TAG, "Geofence successfully added for memo ID: ${memo.id}")
        Unit
    }.onFailure { exception ->
        Log.e(TAG, "Failed to add geofence for memo ID: ${memo.id}. Error: ${exception.message}", exception)
    }

    override suspend fun removeGeofence(memoId: Long): Result<Unit> = runCatching {
        geofencingClient.removeGeofences(listOf(memoId.toString())).await()
        Log.i(TAG, "Geofence successfully removed for memo ID: $memoId")
        Unit
    }.onFailure { exception ->
        Log.e(TAG, "Failed to remove geofence for memo ID: $memoId. Error: ${exception.message}", exception)
    }

    companion object {
        private const val TAG = "GeofenceRepository"
        private const val GEOFENCE_RADIUS_IN_METERS = 200f
    }
}