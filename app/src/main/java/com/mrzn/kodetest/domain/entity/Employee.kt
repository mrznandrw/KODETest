package com.mrzn.kodetest.domain.entity

import java.time.LocalDate

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
)