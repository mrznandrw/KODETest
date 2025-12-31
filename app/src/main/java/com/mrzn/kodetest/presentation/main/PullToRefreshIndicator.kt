package com.mrzn.kodetest.presentation.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun PullToRefreshIndicator(
    isRefreshing: Boolean,
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
    contentVerticalOffset: Dp = 70.dp,
    indicatorDiameter: Dp = 20.dp,
    strokeWidth: Dp = 2.dp,
) {
    val baseOffset = (indicatorDiameter + contentVerticalOffset) / 2

    val indicatorOffset by animateDpAsState(
        targetValue = when {
            isRefreshing -> baseOffset
            state.distanceFraction in 0f..1f -> baseOffset * state.distanceFraction
            state.distanceFraction > 1f -> {
                val extraPull = (state.distanceFraction - 1f) * 0.4f
                baseOffset + contentVerticalOffset * extraPull / 2
            }

            else -> 0.dp
        },
        label = "indicatorOffset"
    )

    Box(
        modifier = modifier
            .size(indicatorDiameter)
            .offset(y = -indicatorDiameter)
            .offset { IntOffset(x = 0, y = indicatorOffset.roundToPx()) },
        contentAlignment = Alignment.Center
    ) {
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.size(indicatorDiameter),
                color = MaterialTheme.colorScheme.inversePrimary,
                strokeWidth = strokeWidth,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
                gapSize = 0.dp
            )
        } else {
            CircularProgressIndicator(
                progress = { state.distanceFraction },
                modifier = Modifier.size(indicatorDiameter),
                color = MaterialTheme.colorScheme.inversePrimary,
                strokeWidth = strokeWidth,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
                gapSize = 0.dp
            )
        }
    }
}
