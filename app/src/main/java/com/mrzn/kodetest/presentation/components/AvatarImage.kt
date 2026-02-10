package com.mrzn.kodetest.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.mrzn.kodetest.R

@Composable
fun AvatarImage(
    avatarUrl: String,
    size: Dp
) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = stringResource(R.string.avatar_employee_description),
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_avatar_placeholder),
        error = painterResource(R.drawable.ic_avatar_placeholder),
        fallback = painterResource(R.drawable.ic_avatar_placeholder)
    )
}