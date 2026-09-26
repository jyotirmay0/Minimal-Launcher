package com.jyotirmay.minimallauncher.domain

/**
 * Pure/testable functions for mapping touch Y coordinates to alphabet letters
 * and computing curve displacement.
 */
object AlphabetTouchMapper {

    private val LETTERS = ('A'..'Z').toList()

    /**
     * Given a touch Y position and the measured top/bottom of the alphabet column,
     * returns the letter at that position.
     *
     * Clamps to first/last letter when touch is outside bounds.
     */
    fun letterForTouchY(
        y: Float,
        top: Float,
        bottom: Float,
        letterCount: Int = LETTERS.size
    ): Char {
        if (letterCount <= 0) return 'A'
        val range = bottom - top
        if (range <= 0f) return 'A'

        val fraction = ((y - top) / range).coerceIn(0f, 1f)
        val index = (fraction * (letterCount - 1)).toInt().coerceIn(0, letterCount - 1)
        return LETTERS[index]
    }

    /**
     * Computes horizontal displacement for a letter at [letterY] given the finger at [fingerY].
     * Uses Gaussian falloff for smooth curve.
     *
     * @param letterY The Y center of this letter
     * @param fingerY The current finger Y position
     * @param maxOffset Maximum horizontal displacement in pixels
     * @param sigma Controls the spread of the curve
     * @return Horizontal offset in pixels (positive = toward center/left)
     */
    fun curveDisplacement(
        letterY: Float,
        fingerY: Float,
        maxOffset: Float,
        sigma: Float
    ): Float {
        val distance = letterY - fingerY
        val influence = kotlin.math.exp(-(distance * distance) / (2f * sigma * sigma))
        return maxOffset * influence.toFloat()
    }
}
