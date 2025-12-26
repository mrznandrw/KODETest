package com.mrzn.kodetest.presentation.main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

@Composable
fun Modifier.shimmerEffect(
    durationMillis: Int = 1000
): Modifier {

    var size by remember { mutableStateOf(IntSize.Zero) }

    val colors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.surfaceVariant,
    )

    val transition = rememberInfiniteTransition()
    val offsetX by transition.animateFloat(
        initialValue = -2f * size.width,
        targetValue = 2f * size.width,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            )
        )
    )

    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(x = offsetX, y = 0f),
                end = Offset(x = offsetX + size.width, y = size.height.toFloat()),
            )
        )
    }.onGloballyPositioned { size = it.size }
}