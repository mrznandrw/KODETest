package com.mrzn.kodetest.data.mapper

import com.mrzn.kodetest.data.network.dto.EmployeeDto
import com.mrzn.kodetest.data.network.dto.ResponseDto
import com.mrzn.kodetest.domain.entity.Department
import com.mrzn.kodetest.domain.entity.Employee
import java.time.LocalDate
import javax.inject.Inject

class EmployeesMapper @Inject constructor() {

    fun mapResponseToEmployees(response: ResponseDto): List<Employee> {
        return response.employees.map { it.toEntity() }
    }

    private fun EmployeeDto.toEntity(): Employee = Employee(
        id = id,
        avatarUrl = avatarUrl,
        firstName = firstName,
        lastName = lastName,
        userTag = userTag,
        department = department.toDepartment(),
        position = position,
        birthday = LocalDate.parse(birthday),
        phone = phone
    )

    private fun String.toDepartment(): Department {
        return when (this) {
            "android" -> Department.ANDROID
            "ios" -> Department.IOS
            "design" -> Department.DESIGN
            "management" -> Department.MANAGEMENT
            "qa" -> Department.QA
            "back_office" -> Department.BACK_OFFICE
            "frontend" -> Department.FRONTEND
            "hr" -> Department.HR
            "pr" -> Department.PR
            "backend" -> Department.BACKEND
            "support" -> Department.SUPPORT
            "analytics" -> Department.ANALYTICS
            else -> {
                throw IllegalArgumentException("Unknown department: $this")
            }
        }
    }
}