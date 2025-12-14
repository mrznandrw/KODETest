package com.mrzn.kodetest.domain.repository

import com.mrzn.kodetest.domain.result.LoadResult
import kotlinx.coroutines.flow.StateFlow

interface Repository {

    fun getEmployees(): StateFlow<LoadResult>

    suspend fun refreshEmployees()
}