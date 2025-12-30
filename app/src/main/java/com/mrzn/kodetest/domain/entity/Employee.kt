package com.mrzn.kodetest.domain.entity

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class Employee(
    val id: String,
    val avatarUrl: String,
    val firstName: String,
    val lastName: String,
    val userTag: String,
    val department: Department,
    val position: String,
    val birthday: LocalDate,
    val phone: String
) {
    val fullName: String
        get() = "$firstName $lastName"
}