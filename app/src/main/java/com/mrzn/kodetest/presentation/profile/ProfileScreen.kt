package com.mrzn.kodetest.presentation.profile

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.mrzn.kodetest.R
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.extensions.age
import com.mrzn.kodetest.extensions.formattedString
import com.mrzn.kodetest.presentation.components.AvatarImage

@Composable
fun Profile(
    employee: Employee,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(bottom = 24.dp)
                .safeContentPadding()
        ) {

            IconButton(
                onClick = onBackPressed,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.icon_back_description),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarImage(
                    avatarUrl = employee.avatarUrl,
                    size = 104.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = employee.fullName,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = employee.userTag,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = employee.position,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            InfoRow(
                iconResId = R.drawable.ic_favorite,
                mainInfo = employee.birthday.formattedString(),
                additionalInfo = employee.birthday.age(context)
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                thickness = 0.5.dp
            )
            InfoRow(
                iconResId = R.drawable.ic_phone,
                mainInfo = employee.phone,
                modifier = Modifier.clickable(onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:${employee.phone}".toUri()
                        }
                    )
                })
            )
        }
    }
}

@Composable
fun InfoRow(
    iconResId: Int,
    mainInfo: String,
    modifier: Modifier = Modifier,
    additionalInfo: String = ""
) {
    Row(
        modifier = modifier.height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = mainInfo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = additionalInfo,
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 16.sp
        )
    }
}