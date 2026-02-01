package com.mrzn.kodetest.extensions

import java.time.LocalDate

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