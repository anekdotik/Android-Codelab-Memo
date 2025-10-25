package com.sap.codelab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.data.local.MemoDao

/**
 * That database that is used to store information.
 */
@Database(entities = [MemoEntity::class], version = 1, exportSchema = false)
internal abstract class MemoDatabase : RoomDatabase() {

    abstract fun getMemoDao(): MemoDao
}