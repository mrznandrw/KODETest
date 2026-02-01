package com.mrzn.kodetest.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.domain.result.LoadResult
import com.mrzn.kodetest.domain.usecase.GetEmployeesUseCase
import com.mrzn.kodetest.domain.usecase.RefreshEmployeesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val refreshEmployeesUseCase: RefreshEmployeesUseCase
) : ViewModel() {

    private var currentSorting: SortType = SortType.ALPHABETICAL
    private val loadedEmployees = mutableListOf<Employee>()

    private val sortListEvents = MutableSharedFlow<Unit>()
    private val sortListFlow: Flow<MainScreenState> = sortListEvents.map {
        (screenState.value as MainScreenState.Employees).copy(
            employees = loadedEmployees.sort(currentSorting),
            sortType = currentSorting
        )
    }

    private val refreshListEvents = MutableSharedFlow<Unit>()
    private val refreshListFlow: Flow<MainScreenState> = refreshListEvents.map {
        if (loadedEmployees.isNotEmpty()) {
            (screenState.value as MainScreenState.Employees).copy(isRefreshing = true)
        } else {
            MainScreenState.Loading
        }
    }

    val loadResultFlow: Flow<MainScreenState> = getEmployeesUseCase().map {
        when (it) {
            is LoadResult.Success -> {
                loadedEmployees.clear()
                loadedEmployees.addAll(it.employees)
                MainScreenState.Employees(
                    employees = it.employees.sort(currentSorting),
                    sortType = currentSorting
                )
            }

            LoadResult.Failure.NoInternet, LoadResult.Failure.ServerError -> {
                if (loadedEmployees.isNotEmpty()) {
                    (screenState.value as MainScreenState.Employees).copy(isRefreshing = false)
                } else {
                    MainScreenState.Error
                }
            }
        }
    }

    fun changeSorting(sortType: SortType) {
        currentSorting = sortType
        viewModelScope.launch {
            sortListEvents.emit(Unit)
        }
    }

    val screenState: StateFlow<MainScreenState> = merge(
        loadResultFlow,
        refreshListFlow,
        sortListFlow
    )
        .onStart { emit(MainScreenState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainScreenState.Initial
        )

    fun refreshList() {
        viewModelScope.launch {
            refreshListEvents.emit(Unit)
            refreshEmployeesUseCase()
        }
    }

    private fun List<Employee>.sort(sortType: SortType) = when (sortType) {
        SortType.ALPHABETICAL -> sortedBy { it.fullName } to emptyList()
        SortType.BIRTHDAY -> {
            sortedBy {
                it.birthday.dayOfYear
            }.partition {
                it.birthday.dayOfYear >= LocalDate.now().dayOfYear
            }
        }
    }
}