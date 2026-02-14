package com.mrzn.kodetest.presentation.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun YearDivider() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfiguredDivider()
        Text(
            text = "${LocalDate.now().year + 1}",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        ConfiguredDivider()
    }
}

@Composable
fun ConfiguredDivider() {
    HorizontalDivider(
        modifier = Modifier.width(72.dp),
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}