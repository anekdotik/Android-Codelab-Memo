package com.sap.codelab.di

import com.sap.codelab.data.repository.GeofenceRepositoryImpl
import com.sap.codelab.domain.repository.GeofenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class GeofenceModule {
    @Binds
    @Singleton
    abstract fun bindGeofenceRepository(
        geofenceRepositoryImpl: GeofenceRepositoryImpl
    ): GeofenceRepository
}