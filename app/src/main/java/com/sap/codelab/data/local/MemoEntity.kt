package com.sap.codelab.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sap.codelab.data.local.MemoDatabase.Companion.MEMO_TABLE_NAME

/**
 * Represents a memo entity in the database.
 * This class is only used within the data layer.
 */
@Entity(
    tableName = MEMO_TABLE_NAME,
    indices = [Index(value = ["isDone"])]
)
internal data class MemoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "reminderDate")
    val reminderDate: Long,

    @ColumnInfo(name = "reminderLatitude")
    val reminderLatitude: Double,

    @ColumnInfo(name = "reminderLongitude")
    val reminderLongitude: Double,

    @ColumnInfo(name = "isDone")
    val isDone: Boolean = false
)
