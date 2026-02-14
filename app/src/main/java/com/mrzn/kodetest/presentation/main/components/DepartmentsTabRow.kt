package com.mrzn.kodetest.presentation.main.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrzn.kodetest.domain.entity.Department
import com.mrzn.kodetest.extensions.labelResId

@Composable
fun DepartmentsTabRow(
    selectedTabIndex: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = rememberDepartmentTabs()

    PrimaryScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedTabIndex,
                    matchContentSize = false
                ),
                width = Dp.Unspecified,
                color = MaterialTheme.colorScheme.inversePrimary
            )
        },
        minTabWidth = 20.dp
    ) {

        tabs.forEachIndexed { index, department ->
            Tab(
                modifier = Modifier.height(36.dp),
                selected = selectedTabIndex == index,
                onClick = { onTabSelect(index) },
                text = {
                    Text(
                        text = stringResource(department.labelResId),
                        fontSize = 15.sp
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
fun rememberDepartmentTabs(): List<Department?> {
    return rememberSaveable { listOf(null) + Department.entries }
}
