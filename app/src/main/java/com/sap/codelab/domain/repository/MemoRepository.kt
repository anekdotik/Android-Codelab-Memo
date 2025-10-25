package com.sap.codelab.domain.repository

import com.sap.codelab.domain.model.Memo
import kotlinx.coroutines.flow.Flow

/**
 * Interface for a repository offering memo related CRUD operations.
 */
interface MemoRepository {

    /**
     * Saves the given memo to the database.
     */
    suspend fun saveMemo(memo: Memo)

    /**
     * @return a Flow of all memos.
     */
    fun getAllMemos(): Flow<List<Memo>>

    /**
     * @return a Flow of all open memos.
     */
    fun getOpenMemos(): Flow<List<Memo>>

    /**
     * @return the memo whose id matches the given id, or null if not found.
     */
    suspend fun getMemoById(id: Long): Memo?
}