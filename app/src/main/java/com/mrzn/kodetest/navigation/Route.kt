package com.mrzn.kodetest.navigation

import androidx.navigation3.runtime.NavKey
import com.mrzn.kodetest.domain.entity.Employee
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data class EmployeeProfile(val employee: Employee) : Route

    @Serializable
    data object EmployeesList : Route
}