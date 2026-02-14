package com.mrzn.kodetest.presentation.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrzn.kodetest.R
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.domain.result.LoadResult
import com.mrzn.kodetest.presentation.getApplicationComponent
import com.mrzn.kodetest.presentation.main.components.BottomSheetSorting
import com.mrzn.kodetest.presentation.main.components.DepartmentsTabRow
import com.mrzn.kodetest.presentation.main.components.EmployeeCard
import com.mrzn.kodetest.presentation.main.components.EmployeeCardSkeleton
import com.mrzn.kodetest.presentation.main.components.EmptySearchResult
import com.mrzn.kodetest.presentation.main.components.ErrorContent
import com.mrzn.kodetest.presentation.main.components.PullToRefreshIndicator
import com.mrzn.kodetest.presentation.main.components.SearchBar
import com.mrzn.kodetest.presentation.main.components.SnackbarVisualsWithError
import com.mrzn.kodetest.presentation.main.components.YearDivider
import com.mrzn.kodetest.presentation.main.components.rememberDepartmentTabs
import kotlinx.coroutines.launch

@Composable
fun MainScreen(onEmployeeClick: (Employee) -> Unit) {

    val component = getApplicationComponent()
    val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsStateWithLifecycle(MainScreenState.Loading)

    when (val currentState = screenState.value) {
        is MainScreenState.Employees -> EmployeesContent(
            state = currentState,
            clearSearch = viewModel::clearSearch,
            onRefresh = viewModel::refreshList,
            onSortingSelect = viewModel::changeSorting,
            onEmployeeClick = onEmployeeClick,
            errorShown = viewModel::errorShown
        )

        MainScreenState.Error -> ErrorContent()
        MainScreenState.Loading -> ContentLoading()
    }
}

@Composable
fun EmployeesContent(
    state: MainScreenState.Employees,
    clearSearch: () -> Unit,
    onRefresh: () -> Unit,
    onSortingSelect: (SortType) -> Unit,
    onEmployeeClick: (Employee) -> Unit,
    errorShown: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isSortByBirthday = (state.sortType == SortType.BIRTHDAY)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    val pullToRefreshState = rememberPullToRefreshState()
    val columnOffset by animateDpAsState(
        targetValue = when {
            state.isRefreshing -> 70.dp
            pullToRefreshState.distanceFraction in 0f..1f -> {
                70.dp * pullToRefreshState.distanceFraction
            }

            pullToRefreshState.distanceFraction > 1 -> {
                70.dp * (1 + (pullToRefreshState.distanceFraction - 1f) * 0.4f)
            }

            else -> 0.dp
        },
        label = "columnOffset"
    )

    val departmentsTabs = rememberDepartmentTabs()
    val pagerState = rememberPagerState { departmentsTabs.size }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isRefreshing) {
        if (state.isRefreshing) {
            snackbarHostState.showSnackbar(
                SnackbarVisualsWithError(
                    message = context.getString(R.string.snackbar_label_refreshing)
                )
            )
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            val message = when (it) {
                LoadResult.Failure.NoInternet -> context.getString(R.string.snackbar_label_no_internet_error)
                LoadResult.Failure.ServerError -> context.getString(R.string.snackbar_label_server_error)
            }

            snackbarHostState.showSnackbar(
                SnackbarVisualsWithError(
                    message = message,
                    isError = true
                )
            )
            errorShown()
        }
    }

    MainScaffold(
        searchQuery = state.searchQuery,
        clearSearch = clearSearch,
        selectedTabIndex = pagerState.targetPage,
        onTabSelect = {
            scope.launch {
                pagerState.animateScrollToPage(it)
            }
        },
        onSortClick = { showBottomSheet = true },
        isSortByBirthday = isSortByBirthday,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->

                val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
                val containerColor = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.inversePrimary
                }

                Snackbar(
                    snackbarData = data,
                    containerColor = containerColor,
                    shape = MaterialTheme.shapes.medium,
                )
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(innerPadding),
            state = pullToRefreshState,
            indicator = {
                PullToRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = state.isRefreshing,
                    state = pullToRefreshState
                )
            }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                state.employees[departmentsTabs[page]]?.let {
                    EmployeesList(
                        employees = it,
                        sortType = state.sortType,
                        modifier = Modifier.offset {
                            IntOffset(x = 0, y = columnOffset.roundToPx())
                        },
                        onEmployeeClick = onEmployeeClick
                    )
                } ?: EmptySearchResult()
            }
        }

        if (showBottomSheet) {
            BottomSheetSorting(
                onDismissRequest = { showBottomSheet = false },
                currentSortType = state.sortType,
                onSortingSelect = onSortingSelect
            )
        }
    }
}

@Composable
fun EmployeesList(
    employees: List<EmployeeListItem>,
    sortType: SortType,
    onEmployeeClick: (Employee) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberSaveable(
        inputs = arrayOf(sortType, employees),
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }

    ScreenLazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(
            items = employees,
            key = { item ->
                when (item) {
                    is EmployeeListItem.EmployeeItem -> item.employee.id
                    EmployeeListItem.YearDivider -> "divider"
                }
            }
        ) { item ->
            when (item) {
                is EmployeeListItem.EmployeeItem -> EmployeeCard(
                    employee = item.employee,
                    showBirthday = item.showBirthday,
                    onClick = onEmployeeClick
                )

                EmployeeListItem.YearDivider -> YearDivider()
            }
        }
    }
}

@Composable
fun ContentLoading() {
    MainScaffold { innerPadding ->
        ScreenLazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(count = 20) {
                EmployeeCardSkeleton()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier,
    searchQuery: TextFieldState = TextFieldState(),
    clearSearch: () -> Unit = {},
    selectedTabIndex: Int = 0,
    onTabSelect: (Int) -> Unit = {},
    onSortClick: () -> Unit = {},
    isSortByBirthday: Boolean = false,
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    expandedHeight = 52.dp,
                    title = {
                        SearchBar(
                            state = searchQuery,
                            clearSearch = clearSearch,
                            onSortClick = onSortClick,
                            modifier = Modifier.padding(end = 16.dp),
                            isSortByBirthday = isSortByBirthday
                        )
                    },
                )

                DepartmentsTabRow(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelect = onTabSelect,
                )
            }
        },
        snackbarHost = snackbarHost,
        content = content
    )
}

@Composable
fun ScreenLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = content
    )
}
