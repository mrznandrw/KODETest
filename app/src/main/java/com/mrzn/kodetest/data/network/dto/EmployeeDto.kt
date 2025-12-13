package com.mrzn.kodetest.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDto(
    @SerialName("id") val id: String,
    @SerialName("avatarUrl") val avatarUrl: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("userTag") val userTag: String,
    @SerialName("department") val department: String,
    @SerialName("position") val position: String,
    @SerialName("birthday") val birthday: String,
    @SerialName("phone") val phone: String
)