package com.mrzn.kodetest.domain.entity

import androidx.compose.runtime.Immutable
import com.mrzn.kodetest.domain.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Immutable
data class Employee(
    val id: String,
    val avatarUrl: String,
    val firstName: String,
    val lastName: String,
    val userTag: String,
    val department: Department,
    val position: String,
    @Serializable(LocalDateSerializer::class)
    val birthday: LocalDate,
    val phone: String
) {
    val fullName: String
        get() = "$firstName $lastName"

    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            fullName,
            "$firstName$lastName",
            "${firstName.first()}${lastName.first()}",
            userTag
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}