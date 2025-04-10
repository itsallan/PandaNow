package io.dala.pandanow.presentation.components.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TVRoundIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = if (isFocused) 0.3f else 0.1f))
            .size(50.dp)
    ) {
        content()
    }
}