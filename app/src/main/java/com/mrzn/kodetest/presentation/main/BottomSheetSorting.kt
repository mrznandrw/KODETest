package com.mrzn.kodetest.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrzn.kodetest.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSorting(
    onDismissRequest: () -> Unit,
    currentSortType: SortType,
    onSortingSelect: (SortType) -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.padding(horizontal = 8.dp),
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 56.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.sort_label),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        RadioGroupSorting(
            currentSortType = currentSortType,
            onSelect = {
                onSortingSelect(it)
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        onDismissRequest()
                    }
                }
            }
        )
    }
}

@Composable
fun RadioGroupSorting(
    currentSortType: SortType,
    onSelect: (SortType) -> Unit
) {
    val radioOptions = SortType.entries.associateWith {
        when (it) {
            SortType.ALPHABETICAL -> stringResource(R.string.sort_by_alphabet)
            SortType.BIRTHDAY -> stringResource(R.string.sort_by_birthday)
        }
    }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        Modifier
            .selectableGroup()
            .padding(vertical = 16.dp)
    ) {
        radioOptions.forEach { sortType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (sortType.key == currentSortType),
                        onClick = { onSelect(sortType.key) },
                        role = Role.RadioButton,
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val iconResId = if (sortType.key == currentSortType) {
                    R.drawable.ic_radio_button_checked
                } else {
                    R.drawable.ic_radio_button_unchecked
                }
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inversePrimary
                )

                Text(
                    text = sortType.value,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
