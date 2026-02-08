package com.mrzn.kodetest.extensions

import android.content.Context
import com.mrzn.kodetest.R
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.Locale

val moy = hashMapOf<Int, String>(
    1 to "янв",
    2 to "фев",
    3 to "мар",
    4 to "апр",
    5 to "май",
    6 to "июн",
    7 to "июл",
    8 to "авг",
    9 to "сен",
    10 to "окт",
    11 to "ноя",
    12 to "дек"
)

fun LocalDate.dayMonth() = "${this.dayOfMonth} ${moy[this.monthValue]}"

fun LocalDate.formattedString(): String {
    val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return sdf.format(Date.valueOf(this.toString()))
}

fun LocalDate.age(context: Context): String {
    val age = Period.between(this, LocalDate.now()).years
    return context.resources.getQuantityString(R.plurals.years_plural, age, age)
}