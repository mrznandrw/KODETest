package com.mrzn.kodetest.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun <T1, T2, T3, R> Flow<T1>.combine(
    flow1: Flow<T2>,
    flow2: Flow<T3>,
    transform: suspend (T1, T2, T3) -> R
): Flow<R> {
    return combine(this, flow1, flow2, transform = transform)
}