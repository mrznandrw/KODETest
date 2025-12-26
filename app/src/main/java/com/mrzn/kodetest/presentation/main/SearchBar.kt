package com.mrzn.kodetest.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrzn.kodetest.R

@Composable
fun SearchBar(modifier: Modifier = Modifier) {

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.inversePrimary,
        backgroundColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.4f)
    )

    val state = rememberTextFieldState()

    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            SearchTextField(
                state = state,
                hasFocus = hasFocus,
                onFocusChanged = {
                    hasFocus = it.isFocused
                },
                onSortClicked = {}
            )
        }

        AnimatedVisibility(visible = hasFocus) {
            TextButton(
                onClick = {
                    focusManager.clearFocus()
                    state.clearText()
                },
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_cancel),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    }
}

@Composable
fun RowScope.SearchTextField(
    state: TextFieldState,
    hasFocus: Boolean,
    onFocusChanged: (FocusState) -> Unit,
    onSortClicked: () -> Unit
) {
    val enabled = true
    val lineLimits = TextFieldLineLimits.SingleLine
    val interactionSource = remember { MutableInteractionSource() }
    val isError = false
    val customColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

    BasicTextField(
        state = state,
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .onFocusChanged {
                onFocusChanged(it)
            },
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary
        ),
        lineLimits = lineLimits,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.inversePrimary),
        decorator = TextFieldDefaults.decorator(
            state = state,
            enabled = enabled,
            lineLimits = lineLimits,
            outputTransformation = null,
            interactionSource = interactionSource,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_placeholder),
                    maxLines = 1,
                    fontSize = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.icon_search_description),
                )
            },
            trailingIcon = {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        hasFocus && state.text.isNotEmpty() -> {
                            IconButton(
                                onClick = {
                                    state.clearText()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_clear),
                                    contentDescription = stringResource(R.string.icon_clear_description),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        !hasFocus -> {
                            IconButton(
                                onClick = onSortClicked
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_list_ui_alt),
                                    contentDescription = stringResource(R.string.icon_sort_description),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            },
            isError = isError,
            colors = customColors,
            contentPadding = PaddingValues(4.dp),
            container = {
                TextFieldDefaults.Container(
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = customColors,
                    shape = RoundedCornerShape(16.dp),
                    focusedIndicatorLineThickness = 0.dp,
                    unfocusedIndicatorLineThickness = 0.dp
                )
            }
        )
    )
}