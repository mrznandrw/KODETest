package com.mrzn.kodetest.domain.usecase

import com.mrzn.kodetest.domain.repository.Repository
import javax.inject.Inject

class RefreshEmployeesUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend operator fun invoke() = repository.refreshEmployees()
}