package com.mrzn.kodetest.data.network.api

import com.mrzn.kodetest.data.network.dto.ResponseDto
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun loadEmployees(): ResponseDto
}