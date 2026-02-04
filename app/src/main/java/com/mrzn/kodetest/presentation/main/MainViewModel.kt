package com.mrzn.kodetest.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.domain.result.LoadResult
import com.mrzn.kodetest.domain.usecase.GetEmployeesUseCase
import com.mrzn.kodetest.domain.usecase.RefreshEmployeesUseCase
import com.mrzn.kodetest.extensions.mergeWith
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val refreshEmployeesUseCase: RefreshEmployeesUseCase
) : ViewModel() {

    private var employees = MutableStateFlow(emptyList<Employee>())
    private val isRefreshing = MutableStateFlow(false)
    private var currentSorting = MutableStateFlow(SortType.ALPHABETICAL)

    private val errorEvents = MutableSharedFlow<Unit>()
    private val errorFlow = errorEvents.map {
        MainScreenState.Error
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            getEmployeesUseCase()
                .collect {
                    when (it) {
                        is LoadResult.Success -> {
                            if (employees.value == it.employees) isRefreshing.value = false
                            employees.value = it.employees
                        }

                        LoadResult.Failure.NoInternet, LoadResult.Failure.ServerError -> {
                            isRefreshing.value = false
                            if (employees.value.isNotEmpty()) {
                                //todo show snackbar with error
                            } else {
                                errorEvents.emit(Unit)
                            }
                        }
                    }
                }
        }
    }

    private val sortedEmployees = employees
        .filter { it.isNotEmpty() }
        .combine(currentSorting) { employees, sorting ->
            employees.sort(sorting)
        }

    val screenState: StateFlow<MainScreenState> = sortedEmployees
        .map {
            MainScreenState.Employees(
                employees = it,
                sortType = currentSorting.value
            )
        }
        .onEach { isRefreshing.value = false }
        .mergeWith(errorFlow)
        .onStart {
            emit(MainScreenState.Loading)
            loadEmployees()
        }
        .combine(isRefreshing) { screenState, isRefreshing ->
            when (screenState) {
                is MainScreenState.Employees -> screenState.copy(isRefreshing = isRefreshing)
                MainScreenState.Error if isRefreshing -> MainScreenState.Loading
                else -> screenState
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainScreenState.Initial
        )

    fun refreshList() {
        isRefreshing.value = true
        viewModelScope.launch {
            refreshEmployeesUseCase()
        }
    }

    fun changeSorting(sortType: SortType) {
        currentSorting.value = sortType
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