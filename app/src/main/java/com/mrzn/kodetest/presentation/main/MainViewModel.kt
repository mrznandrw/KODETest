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
import com.mrzn.kodetest.extensions.combine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
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
    private val error = MutableStateFlow<LoadResult.Failure?>(null)

    private val searchQuery = TextFieldState()
    private val searchFlow = snapshotFlow { searchQuery.text.trim() }.debounce(500)

    private fun loadEmployees() {
        viewModelScope.launch {
            getEmployeesUseCase()
                .collect {
                    when (it) {
                        is LoadResult.Success -> {
                            if (employees.value == it.employees) isRefreshing.value = false
                            employees.value = it.employees
                        }

                        is LoadResult.Failure -> {
                            isRefreshing.value = false
                            error.value = it
                        }
                    }
                }
        }
    }

    private val sortedEmployees = combine(
        employees.filter { it.isNotEmpty() },
        searchFlow,
        currentSorting
    ) { employees, query, sorting ->
        val filtered = if (query.isBlank()) {
            employees
        } else {
            employees.filter {
                it.doesMatchSearchQuery(query.toString())
            }
        }

        val sorted = filtered.sort(sorting)

        buildMap {
            if (sorted.isNotEmpty()) {
                put(null, sorted)
            }
            putAll(sorted.groupBy { it.department })
        }.mapValues { (_, value) ->
            value.toUiItems(sorting)
        }
    }.flowOn(Dispatchers.Default)

    val screenState: StateFlow<MainScreenState> = sortedEmployees
        .map {
            MainScreenState.Employees(
                employees = it,
                sortType = currentSorting.value,
                searchQuery = searchQuery
            ) as MainScreenState
        }
        .onEach { isRefreshing.value = false }
        .onStart {
            loadEmployees()
            emit(MainScreenState.Loading)
        }
        .combine(
            isRefreshing.onEach { if (it) errorShown() },
            error
        ) { screenState, isRefreshing, error ->
            InputData(screenState, isRefreshing, error)
        }
        .scan(MainScreenState.Loading as MainScreenState) { prevState, (state, isRefreshing, error) ->
            when (prevState) {
                is MainScreenState.Loading if state is MainScreenState.Employees -> state
                is MainScreenState.Loading if (error != null) -> MainScreenState.Error
                is MainScreenState.Error if isRefreshing -> MainScreenState.Loading
                is MainScreenState.Employees -> (state as MainScreenState.Employees).copy(
                    isRefreshing = isRefreshing,
                    error = error
                )

                else -> prevState
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainScreenState.Loading
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

    fun errorShown() {
        error.value = null
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

    private data class InputData(
        val screenState: MainScreenState,
        val isRefreshing: Boolean,
        val error: LoadResult.Failure?
    )
}