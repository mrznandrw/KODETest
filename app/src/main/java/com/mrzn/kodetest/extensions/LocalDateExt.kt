package com.mrzn.kodetest.extensions

import android.content.Context
import com.mrzn.kodetest.R
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.Locale

fun LocalDate.dayMonth(context: Context): String {
    val months = context.resources.getStringArray(R.array.months_short)
    return "${this.dayOfMonth} ${months[this.monthValue - 1]}"
}

fun LocalDate.formattedString(): String {
    val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return sdf.format(Date.valueOf(this.toString()))
}

fun LocalDate.age(context: Context): String {
    val age = Period.between(this, LocalDate.now()).years
    return context.resources.getQuantityString(R.plurals.years_plural, age, age)
}