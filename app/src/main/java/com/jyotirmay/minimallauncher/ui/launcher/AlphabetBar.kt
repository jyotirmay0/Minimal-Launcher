package com.jyotirmay.minimallauncher.ui.launcher

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jyotirmay.minimallauncher.domain.AlphabetTouchMapper
import kotlinx.coroutines.launch
import android.view.HapticFeedbackConstants
import kotlin.math.roundToInt

private val LETTERS = ('A'..'Z').toList()

/**
 * Vertical A–Z alphabet bar pinned to the right edge.
 *
 * Features:
 * - Star icon at top
 * - Compact A–Z letters
 * - Dot indicator at bottom
 * - Gaussian curve deformation during touch
 * - Spring-back animation on release
 * - Haptic tick on letter change
 * - Letter bubble showing selected letter
 */
@Composable
fun AlphabetBar(
    availableLetters: Set<Char>,
    selectedLetter: Char?,
    fingerY: Float?,
    isActive: Boolean,
    onTouchStart: (Char, Float) -> Unit,
    onDrag: (Char, Float) -> Unit,
    onRelease: () -> Unit,
    onStarTap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    // Track letter positions (center Y in parent coordinates)
    val letterPositions = remember { FloatArray(LETTERS.size) }
    var alphabetTop by remember { mutableFloatStateOf(0f) }
    var alphabetBottom by remember { mutableFloatStateOf(0f) }
    var alphabetGlobalTop by remember { mutableFloatStateOf(0f) }

    // Spring animation for curve displacement
    val springProgress = remember { Animatable(0f) }
    var lastActiveFingerY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(fingerY) {
        if (fingerY != null) {
            lastActiveFingerY = fingerY
        }
    }

    // Previous selected letter for haptic
    var previousLetter by remember { mutableStateOf<Char?>(null) }

    // Trigger haptic on letter change
    LaunchedEffect(selectedLetter) {
        if (selectedLetter != null && selectedLetter != previousLetter) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            previousLetter = selectedLetter
        }
        if (selectedLetter == null) {
            previousLetter = null
        }
    }

    // Animate spring on release
    LaunchedEffect(isActive) {
        if (isActive) {
            springProgress.snapTo(1f)
        } else {
            springProgress.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.65f,
                    stiffness = 380f
                )
            )
        }
    }

    val maxOffsetPx = with(density) { 48.dp.toPx() }
    val sigmaPx = with(density) { 60.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(44.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val letter = AlphabetTouchMapper.letterForTouchY(
                            y = offset.y,
                            top = alphabetTop,
                            bottom = alphabetBottom
                        )
                        onTouchStart(letter, offset.y + alphabetGlobalTop)
                    },
                    onDragEnd = {
                        onRelease()
                    },
                    onDragCancel = {
                        onRelease()
                    },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        val letter = AlphabetTouchMapper.letterForTouchY(
                            y = change.position.y,
                            top = alphabetTop,
                            bottom = alphabetBottom
                        )
                        onDrag(letter, change.position.y + alphabetGlobalTop)
                    }
                )
            }
            .semantics {
                contentDescription = "Alphabet navigation, A to Z"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    alphabetGlobalTop = coords.positionInParent().y
                }
        ) {
            // Star at top (tap to return to Home)
            Text(
                text = "☆",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onStarTap() }
                    .padding(bottom = 2.dp)
            )

            // A–Z letters
            LETTERS.forEachIndexed { index, letter ->
                val isAvailable = letter in availableLetters
                val alpha = if (isAvailable) 0.8f else 0.25f

                // Calculate curve displacement - uses lastActiveFingerY during spring-back release
                val effectiveFingerY = fingerY ?: if (springProgress.value > 0.001f) lastActiveFingerY else null
                val displacement = if (effectiveFingerY != null && springProgress.value > 0.001f) {
                    val letterCenterY = letterPositions[index]
                    val localFingerY = effectiveFingerY - alphabetGlobalTop
                    AlphabetTouchMapper.curveDisplacement(
                        letterY = letterCenterY,
                        fingerY = localFingerY,
                        maxOffset = maxOffsetPx,
                        sigma = sigmaPx
                    ) * springProgress.value
                } else {
                    0f
                }

                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .onGloballyPositioned { coords ->
                            val posInParent = coords.positionInParent()
                            val centerY = posInParent.y + coords.size.height / 2f
                            letterPositions[index] = centerY

                            if (index == 0) alphabetTop = centerY
                            if (index == LETTERS.size - 1) alphabetBottom = centerY
                        }
                        .offset { IntOffset(x = -displacement.roundToInt(), y = 0) }
                        .padding(vertical = 0.5.dp)
                )
            }

            // Dot indicator at bottom
            Text(
                text = "•",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Letter bubble
        if (selectedLetter != null && fingerY != null && isActive) {
            val selectedIndex = LETTERS.indexOf(selectedLetter)
            if (selectedIndex >= 0) {
                val bubbleY = letterPositions[selectedIndex]
                LetterBubble(
                    letter = selectedLetter,
                    yPosition = bubbleY,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }
        }
    }
}
