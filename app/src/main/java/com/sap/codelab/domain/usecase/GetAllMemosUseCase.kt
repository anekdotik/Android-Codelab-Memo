package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all memos.
 */
class GetAllMemosUseCase @Inject constructor(
    private val repository: MemoRepository
) {
    operator fun invoke(): Flow<List<Memo>> {
        return repository.getAllMemos()
    }
}