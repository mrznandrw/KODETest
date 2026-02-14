package com.mrzn.kodetest.presentation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mrzn.kodetest.di.ApplicationComponent
import com.mrzn.kodetest.di.DaggerApplicationComponent

class EmployeesApplication : Application() {

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.create()
    }
}

@Composable
fun getApplicationComponent(): ApplicationComponent {
    return (LocalContext.current.applicationContext as EmployeesApplication).component
}