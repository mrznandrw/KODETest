package com.mrzn.kodetest.domain.result

import com.mrzn.kodetest.domain.entity.Employee

sealed class LoadResult {

    data object Initial : LoadResult()

    data object Loading : LoadResult()

    data class Success(val employees: List<Employee>) : LoadResult()

    sealed class Failure() : LoadResult() {

        data object NoInternet : Failure()

        data object ServerError : Failure()
    }
}