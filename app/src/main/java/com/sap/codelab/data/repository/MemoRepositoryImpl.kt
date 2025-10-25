package com.sap.codelab.data.repository

import com.sap.codelab.data.local.MemoDao
import com.sap.codelab.data.mapper.toDomain
import com.sap.codelab.data.mapper.toEntity
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * The repository is used to retrieve data from a data source.
 */
internal class MemoRepositoryImpl @Inject constructor(
    private val memoDao: MemoDao
) : MemoRepository {

    override suspend fun saveMemo(memo: Memo) {
        memoDao.insert(memo.toEntity())
    }

    override fun getOpenMemos(): Flow<List<Memo>> {
        return memoDao.getOpen().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllMemos(): Flow<List<Memo>> {
        return memoDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMemoById(id: Long): Memo? {
        return memoDao.getMemoById(id)?.toDomain()
    }
}