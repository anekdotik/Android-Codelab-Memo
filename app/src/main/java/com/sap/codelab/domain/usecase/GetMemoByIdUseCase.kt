package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository

/**
 * Use case for retrieving a single memo by its ID.
 */
class GetMemoByIdUseCase @Inject constructor(
    private val repository: MemoRepository
) {
    suspend operator fun invoke(memoId: Long): Memo? {
        return repository.getMemoById(memoId)
    }
}