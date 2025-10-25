package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository


/**
 * Use case for saving or updating a Memo.
 */
class SaveMemoUseCase @Inject constructor(
    private val repository: MemoRepository
) {
    suspend operator fun invoke(memo: Memo) {
        repository.saveMemo(memo)
    }
}