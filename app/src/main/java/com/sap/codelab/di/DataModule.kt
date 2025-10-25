package com.sap.codelab.di

import android.content.Context
import androidx.room.Room
import com.sap.codelab.data.local.MemoDao
import com.sap.codelab.data.local.MemoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    @Singleton
    fun provideMemoDatabase(@ApplicationContext context: Context): MemoDatabase {
        return Room.databaseBuilder(
            context,
            MemoDatabase::class.java,
            "memo_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMemoDao(database: MemoDatabase): MemoDao {
        return database.getMemoDao()
    }
}