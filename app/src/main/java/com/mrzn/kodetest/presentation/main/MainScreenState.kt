package com.mrzn.kodetest.presentation.main

import com.mrzn.kodetest.domain.entity.Employee

sealed class MainScreenState {

    data object Initial : MainScreenState()

    data object Loading : MainScreenState()

    data class Employees(
        val employees: Pair<List<Employee>, List<Employee>>,
        val sortType: SortType,
        val isRefreshing: Boolean = false
    ) : MainScreenState()

    data object Error : MainScreenState()
}