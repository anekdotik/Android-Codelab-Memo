package com.sap.codelab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sap.codelab.data.local.MemoDatabase.Companion.DATABASE_EXPORT_SCHEME
import com.sap.codelab.data.local.MemoDatabase.Companion.DATABASE_VERSION

/**
 * That database that is used to store information.
 */
@Database(entities = [MemoEntity::class], version = DATABASE_VERSION, exportSchema = DATABASE_EXPORT_SCHEME)
internal abstract class MemoDatabase : RoomDatabase() {

    abstract fun getMemoDao(): MemoDao

    companion object {
        const val DATABASE_NAME = "memo_database"
        const val DATABASE_VERSION = 1
        const val DATABASE_EXPORT_SCHEME = false
        const val MEMO_TABLE_NAME = "memo"
    }
}