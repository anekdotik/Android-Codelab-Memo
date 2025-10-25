package com.sap.codelab.data.mapper

import com.sap.codelab.data.local.MemoEntity
import com.sap.codelab.domain.model.Memo

/**
 * Converts a MemoEntity from the data layer to a Memo domain model.
 */
internal fun MemoEntity.toDomain(): Memo {
    return Memo(
        id = this.id,
        title = this.title,
        description = this.description,
        reminderDate = this.reminderDate,
        reminderLatitude = this.reminderLatitude,
        reminderLongitude = this.reminderLongitude,
        isDone = this.isDone
    )
}

/**
 * Converts a Memo domain model to a MemoEntity for the data layer.
 */
internal fun Memo.toEntity(): MemoEntity {
    return MemoEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        reminderDate = this.reminderDate,
        reminderLatitude = this.reminderLatitude,
        reminderLongitude = this.reminderLongitude,
        isDone = this.isDone
    )
}