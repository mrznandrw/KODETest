package com.mrzn.kodetest.presentation.main.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

class SnackbarVisualsWithError(
    override val message: String,
    val isError: Boolean = false
) : SnackbarVisuals {
    override val actionLabel: String? = null

    override val withDismissAction: Boolean = false

    override val duration: SnackbarDuration
        get() = if (isError) SnackbarDuration.Short else SnackbarDuration.Indefinite
}