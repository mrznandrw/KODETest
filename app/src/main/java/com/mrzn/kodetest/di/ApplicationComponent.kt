package com.mrzn.kodetest.di

import com.mrzn.kodetest.presentation.ViewModelFactory
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun getViewModelFactory(): ViewModelFactory
}