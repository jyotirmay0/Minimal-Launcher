package com.jyotirmay.minimallauncher.ui.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Circular bubble showing the currently selected letter.
 * Positioned to the left of the finger position in the alphabet bar.
 */
@Composable
fun LetterBubble(
    letter: Char,
    yPosition: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val bubbleSize = 48.dp
    val bubbleSizePx = with(density) { bubbleSize.toPx() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .offset {
                IntOffset(
                    x = with(density) { (-56).dp.toPx().roundToInt() },
                    y = (yPosition - bubbleSizePx / 2).roundToInt()
                )
            }
            .size(bubbleSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
