package com.mrzn.kodetest.domain.usecase

import com.mrzn.kodetest.domain.repository.Repository
import com.mrzn.kodetest.domain.result.LoadResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(
    private val repository: Repository
) {

    operator fun invoke(): Flow<LoadResult> = repository.employees
}