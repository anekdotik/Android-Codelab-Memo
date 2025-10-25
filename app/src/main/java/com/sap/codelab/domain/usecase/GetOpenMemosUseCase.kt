package com.sap.codelab.domain.usecase

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving only open (not done) memos.
 */
class GetOpenMemosUseCase @Inject constructor(
    private val repository: MemoRepository
) {
    operator fun invoke(): Flow<List<Memo>> {
        return repository.getOpenMemos()
    }
}