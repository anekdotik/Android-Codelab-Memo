package com.sap.codelab.domain.model

/**
 * Represents a memo.
 */
data class Memo(
    val id: Long = 0,
    val title: String,
    val description: String,
    val reminderDate: Long = 0,
    val reminderLatitude: Double,
    val reminderLongitude: Double,
    val isDone: Boolean = false
)