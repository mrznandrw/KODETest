package com.mrzn.kodetest.presentation.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.domain.result.LoadResult
import com.mrzn.kodetest.domain.usecase.GetEmployeesUseCase
import com.mrzn.kodetest.domain.usecase.RefreshEmployeesUseCase
import com.mrzn.kodetest.extensions.mergeWith
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(FlowPreview::class)
class MainViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val refreshEmployeesUseCase: RefreshEmployeesUseCase
) : ViewModel() {

    private val employees = MutableStateFlow(emptyList<Employee>())
    private val isRefreshing = MutableStateFlow(false)
    private val currentSorting = MutableStateFlow(SortType.ALPHABETICAL)

    private val searchQuery = TextFieldState()
    private val searchFlow = snapshotFlow { searchQuery.text.trim() }.debounce(500)

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
        .combine(searchFlow) { employees, query ->
            if (query.isBlank()) {
                employees
            } else {
                employees.filter {
                    it.doesMatchSearchQuery(query.toString())
                }
            }
        }
        .combine(currentSorting) { employees, sorting ->
            val sorted = employees.sort(sorting)

            buildMap {
                put(null, sorted)
                putAll(sorted.groupBy { it.department })
            }.mapValues { (_, value) ->
                value.toUiItems(sorting)
            }
        }

    val screenState: StateFlow<MainScreenState> = sortedEmployees
        .map {
            MainScreenState.Employees(
                employees = it,
                sortType = currentSorting.value,
                searchQuery = searchQuery
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

    fun clearSearch() {
        searchQuery.clearText()
    }

    private fun List<Employee>.sort(sortType: SortType): List<Employee> = when (sortType) {
        SortType.ALPHABETICAL -> sortedBy { it.fullName }
        SortType.BIRTHDAY -> sortedBy { it.birthday.dayOfYear }
    }

    private fun List<Employee>.toUiItems(sortType: SortType): List<EmployeeListItem> =
        when (sortType) {
            SortType.ALPHABETICAL -> map { it.toEmployeeListItem() }

            SortType.BIRTHDAY -> {
                val today = LocalDate.now().dayOfYear
                val (upcoming, previous) = partition {
                    it.birthday.dayOfYear >= today
                }

                buildList {
                    addAll(upcoming.map { it.toEmployeeListItem(true) })
                    if (previous.isNotEmpty()) {
                        add(EmployeeListItem.YearDivider)
                        addAll(previous.map { it.toEmployeeListItem(true) })
                    }
                }
            }
        }

    private fun Employee.toEmployeeListItem(showBirthday: Boolean = false) =
        EmployeeListItem.EmployeeItem(
            employee = this,
            showBirthday = showBirthday
        )

}