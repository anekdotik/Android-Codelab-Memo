package com.sap.codelab.domain.repository

import com.sap.codelab.domain.model.Memo

interface GeofenceRepository {
    suspend fun addGeofence(memo: Memo, radiusInMeters: Float): Result<Unit>
    suspend fun removeGeofence(memoId: Long): Result<Unit>
}