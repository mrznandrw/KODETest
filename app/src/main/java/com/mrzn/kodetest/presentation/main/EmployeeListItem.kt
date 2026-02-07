package com.mrzn.kodetest.presentation.main

import com.mrzn.kodetest.domain.entity.Employee

sealed class EmployeeListItem {

    data class EmployeeItem(
        val employee: Employee,
        val showBirthday: Boolean
    ) : EmployeeListItem()

    data object YearDivider : EmployeeListItem()
}