package com.mrzn.kodetest.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrzn.kodetest.domain.result.LoadResult
import com.mrzn.kodetest.domain.usecase.GetEmployeesUseCase
import com.mrzn.kodetest.domain.usecase.RefreshEmployeesUseCase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val refreshEmployeesUseCase: RefreshEmployeesUseCase
) : ViewModel() {

    val screenState = getEmployeesUseCase().map {
        when (it) {
            is LoadResult.Success -> MainScreenState.Employees(employees = it.employees)
            LoadResult.Failure.NoInternet, LoadResult.Failure.ServerError -> MainScreenState.Error
            LoadResult.Initial -> MainScreenState.Initial
            LoadResult.Loading -> MainScreenState.Loading
        }
    }

    fun refreshList() {
        viewModelScope.launch {
            refreshEmployeesUseCase()
        }
    }
}