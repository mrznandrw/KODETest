package com.mrzn.kodetest.data.network.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

object ApiFactory {

    private const val BASE_URL = "https://stoplight.io/mocks/kode-api/trainee-test/331141861/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json; charset=utf-8".toMediaType()
            )
        )
        .build()

    val apiService: ApiService = retrofit.create()
}