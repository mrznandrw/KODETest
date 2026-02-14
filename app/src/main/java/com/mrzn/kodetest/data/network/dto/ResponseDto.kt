package com.mrzn.kodetest.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    @SerialName("items") val employees: List<EmployeeDto>
)
