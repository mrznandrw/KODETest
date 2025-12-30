package com.mrzn.kodetest.domain.repository

import com.mrzn.kodetest.domain.result.LoadResult
import kotlinx.coroutines.flow.Flow

interface Repository {

    val employees: Flow<LoadResult>

    suspend fun refreshEmployees()
}