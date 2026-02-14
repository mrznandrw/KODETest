package com.mrzn.kodetest.presentation.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrzn.kodetest.R
import com.mrzn.kodetest.presentation.getApplicationComponent
import com.mrzn.kodetest.presentation.main.MainViewModel

@Composable
fun ErrorContent() {

    val component = getApplicationComponent()
    val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())

    LaunchedEffect(Unit) {
        viewModel.errorShown()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_flying_saucer),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.critical_error_title),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.critical_error_subtitle),
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = viewModel::refreshList,
            modifier = Modifier.height(20.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = stringResource(R.string.button_try_again),
                color = MaterialTheme.colorScheme.inversePrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}