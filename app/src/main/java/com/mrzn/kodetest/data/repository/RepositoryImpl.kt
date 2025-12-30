package com.mrzn.kodetest.data.repository

import com.mrzn.kodetest.data.mapper.EmployeesMapper
import com.mrzn.kodetest.data.network.api.ApiService
import com.mrzn.kodetest.domain.repository.Repository
import com.mrzn.kodetest.domain.result.LoadResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: EmployeesMapper
) : Repository {

    private val loadEmployeesEvents = MutableSharedFlow<Unit>()

    private val loadEmployees = flow {
        emit(LoadResult.Loading)

        val response = apiService.loadEmployees()
        val employees = mapper.mapResponseToEmployees(response)

        emit(LoadResult.Success(employees))
    }.catch {
        emit(
            when (it) {
                is HttpException -> LoadResult.Failure.ServerError
                else -> LoadResult.Failure.NoInternet
            }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val employees: Flow<LoadResult> = loadEmployeesEvents
        .onStart { emit(Unit) }
        .flatMapLatest { loadEmployees }

    override suspend fun refreshEmployees() {
        loadEmployeesEvents.emit(Unit)
    }
}