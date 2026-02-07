package com.mrzn.kodetest.presentation.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.mrzn.kodetest.R
import com.mrzn.kodetest.domain.entity.Department
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.extensions.dayMonth
import com.mrzn.kodetest.extensions.labelResId
import com.mrzn.kodetest.presentation.getApplicationComponent
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {

    val component = getApplicationComponent()
    val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState(MainScreenState.Initial)

    when (val currentState = screenState.value) {
        is MainScreenState.Employees -> EmployeesContent(
            state = currentState,
            clearSearch = viewModel::clearSearch,
            onRefresh = viewModel::refreshList,
            onSortingSelect = viewModel::changeSorting
        )

        MainScreenState.Error -> ErrorContent()
        MainScreenState.Loading -> ContentLoading()
        MainScreenState.Initial -> {}
    }
}

@Composable
fun EmployeesContent(
    state: MainScreenState.Employees,
    clearSearch: () -> Unit,
    onRefresh: () -> Unit,
    onSortingSelect: (SortType) -> Unit
) {

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
    val scope = rememberCoroutineScope()

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
        isSortByBirthday = isSortByBirthday
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
                        }
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
                    modifier = Modifier.padding(vertical = 4.dp)
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
                EmployeeCardSkeleton(modifier = Modifier.padding(vertical = 4.dp))
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = content
    )
}

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

@Composable
fun EmployeeCard(
    employee: Employee,
    showBirthday: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = employee.avatarUrl,
            contentDescription = stringResource(R.string.avatar_employee_description),
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            placeholder = painterResource(R.drawable.ic_avatar_placeholder),
            error = painterResource(R.drawable.ic_avatar_placeholder),
            fallback = painterResource(R.drawable.ic_avatar_placeholder)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Row {
                Text(
                    text = employee.fullName,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = employee.userTag,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp
                )
            }
            Text(
                text = employee.position,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (showBirthday) {
            Text(
                text = employee.birthday.dayMonth(),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun EmployeeCardSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Box(
                modifier = Modifier
                    .size(144.dp, 16.dp)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .size(80.dp, 13.dp)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect()
            )
        }
    }
}
