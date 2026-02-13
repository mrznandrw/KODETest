package com.mrzn.kodetest.presentation.main

import androidx.compose.foundation.text.input.TextFieldState
import com.mrzn.kodetest.domain.entity.Department
import com.mrzn.kodetest.domain.result.LoadResult

sealed class MainScreenState {

    data object Loading : MainScreenState()

    data class Employees(
        val employees: Map<Department?, List<EmployeeListItem>>,
        val sortType: SortType,
        val searchQuery: TextFieldState,
        val isRefreshing: Boolean = false,
        val error: LoadResult.Failure? = null
    ) : MainScreenState()

    data object Error : MainScreenState()
}