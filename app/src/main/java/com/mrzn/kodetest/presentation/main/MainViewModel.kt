package com.mrzn.kodetest.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrzn.kodetest.domain.result.LoadResult
import com.mrzn.kodetest.domain.usecase.GetEmployeesUseCase
import com.mrzn.kodetest.domain.usecase.RefreshEmployeesUseCase
import com.mrzn.kodetest.extensions.mergeWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val refreshEmployeesUseCase: RefreshEmployeesUseCase
) : ViewModel() {

    private val refreshListEvents = MutableSharedFlow<Unit>()

    private val refreshListFlow: Flow<MainScreenState> = refreshListEvents.map {
        val currentState = screenState.value

        if (currentState is MainScreenState.Employees) {
            currentState.copy(isRefreshing = true)
        } else {
            MainScreenState.Loading
        }
    }

    val loadResultFlow: Flow<MainScreenState> = getEmployeesUseCase().map {
        when (it) {
            is LoadResult.Success -> MainScreenState.Employees(employees = it.employees)
            LoadResult.Failure.NoInternet, LoadResult.Failure.ServerError -> {
                val currentState = screenState.value
                if (currentState is MainScreenState.Employees){
                    currentState.copy(isRefreshing = false)
                }else{
                    MainScreenState.Error
                }
            }
        }
    }

    val screenState: StateFlow<MainScreenState> = loadResultFlow
        .onStart { emit(MainScreenState.Loading) }
        .mergeWith(refreshListFlow)
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
}