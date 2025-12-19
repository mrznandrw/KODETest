package com.mrzn.kodetest.data.repository

import com.mrzn.kodetest.data.mapper.EmployeesMapper
import com.mrzn.kodetest.data.network.api.ApiService
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.domain.repository.Repository
import com.mrzn.kodetest.domain.result.LoadResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import retrofit2.HttpException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: EmployeesMapper
) : Repository {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _employees = mutableListOf<Employee>()
    private val employees: List<Employee>
        get() = _employees.toList()

    private val loadEmployeesEvents = MutableSharedFlow<Unit>(replay = 1)

    private val loadEmployees: StateFlow<LoadResult> = flow {
        loadEmployeesEvents.emit(Unit)
        loadEmployeesEvents.collect {
            emit(LoadResult.Loading)
            val response = apiService.loadEmployees()
            _employees.addAll(mapper.mapResponseToEmployees(response))
            emit(LoadResult.Success(employees))
        }
    }.catch {
        emit(
            when (it) {
                is HttpException -> LoadResult.Failure.ServerError
                else -> LoadResult.Failure.NoInternet
            }
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = LoadResult.Initial
    )

    override fun getEmployees(): StateFlow<LoadResult> = loadEmployees

    override suspend fun refreshEmployees() {
        loadEmployeesEvents.emit(Unit)
    }
}