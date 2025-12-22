package com.mrzn.kodetest.di

import com.mrzn.kodetest.data.network.api.ApiFactory
import com.mrzn.kodetest.data.network.api.ApiService
import com.mrzn.kodetest.data.repository.RepositoryImpl
import com.mrzn.kodetest.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindRepository(impl: RepositoryImpl): Repository

    companion object {

        @[ApplicationScope Provides]
        fun provideApiService(): ApiService = ApiFactory.apiService
    }
}