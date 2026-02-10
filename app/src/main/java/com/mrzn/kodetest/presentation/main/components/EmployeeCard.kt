package com.mrzn.kodetest.presentation.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrzn.kodetest.domain.entity.Employee
import com.mrzn.kodetest.extensions.dayMonth
import com.mrzn.kodetest.presentation.components.AvatarImage

@Composable
fun EmployeeCard(
    employee: Employee,
    showBirthday: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Employee) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(employee) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarImage(avatarUrl = employee.avatarUrl, size = 72.dp)

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
                text = employee.birthday.dayMonth(LocalContext.current),
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