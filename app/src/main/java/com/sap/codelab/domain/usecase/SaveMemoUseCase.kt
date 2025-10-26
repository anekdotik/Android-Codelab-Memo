package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.GeofenceRepository
import com.sap.codelab.domain.repository.MemoRepository
import javax.inject.Inject


/**
 * Use case for saving or updating a Memo.
 */
class SaveMemoUseCase @Inject constructor(
    private val memoRepository: MemoRepository,
    private val geofenceRepository: GeofenceRepository
) {
    suspend operator fun invoke(memo: Memo) {
        val newId = memoRepository.saveMemo(memo)

        if (memo.reminderLatitude != 0.0 || memo.reminderLongitude != 0.0) {
            val memoWithId = memo.copy(id = newId)
            geofenceRepository.addGeofence(memoWithId)
        }
    }
}