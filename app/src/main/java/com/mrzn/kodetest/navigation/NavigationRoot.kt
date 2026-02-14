package com.mrzn.kodetest.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.mrzn.kodetest.presentation.main.MainScreen
import com.mrzn.kodetest.presentation.profile.Profile

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack(Route.EmployeesList)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.EmployeeProfile> { key ->
                Profile(
                    employee = key.employee,
                    onBackPressed = { backStack.removeLastOrNull() })
            }
            entry<Route.EmployeesList> {
                MainScreen(
                    onEmployeeClick = { employee ->
                        backStack.add(Route.EmployeeProfile(employee))
                    }
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it }
            ) togetherWith slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it }
            ) togetherWith slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it }
            ) togetherWith slideOutHorizontally(targetOffsetX = { it })
        }
    )
}