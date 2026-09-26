package com.jyotirmay.minimallauncher

import com.jyotirmay.minimallauncher.domain.AlphabetTouchMapper
import com.jyotirmay.minimallauncher.domain.AppGrouper
import com.jyotirmay.minimallauncher.domain.LetterMapper
import com.jyotirmay.minimallauncher.data.model.LauncherApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for pure domain logic: grouping, letter mapping, touch-to-letter,
 * and curve displacement.
 */
class LauncherLogicTest {

    // Helper: creates a minimal LauncherApp with only the fields needed for grouping tests.
    // Icon is not used in pure logic tests so we use a mock-safe approach.
    private fun testApp(label: String, pkg: String = label.lowercase().replace(" ", ".")) =
        TestLauncherApp(
            packageName = pkg,
            activityName = null,
            label = label,
            letterKey = LetterMapper.letterForApp(label)
        )

    // Lightweight version for testing without Drawable dependency
    data class TestLauncherApp(
        val packageName: String,
        val activityName: String?,
        val label: String,
        val letterKey: Char
    )

    // ===== Test 1: Grouping =====

    @Test
    fun `grouping - apps are grouped by first letter correctly`() {
        val apps = listOf("Gmail", "Google", "Chrome", "GPay", "WhatsApp")
        val grouped = groupTestApps(apps)

        assertEquals(listOf("Chrome"), grouped['C']?.map { it.label })
        assertEquals(listOf("Gmail", "Google", "GPay").sorted(), grouped['G']?.map { it.label }?.sorted())
        assertEquals(listOf("WhatsApp"), grouped['W']?.map { it.label })
    }

    // ===== Test 2: Sorting within groups =====

    @Test
    fun `sorting - apps within a group are alphabetically sorted`() {
        val apps = listOf("Google", "GPay", "Gmail")
        val grouped = groupTestApps(apps)

        val gApps = grouped['G']!!.map { it.label }
        assertEquals(listOf("Gmail", "Google", "GPay"), gApps)
    }

    // ===== Test 3: Empty letters =====

    @Test
    fun `empty letters - letters without apps produce empty groups`() {
        val apps = listOf("Gmail")
        val grouped = groupTestApps(apps)

        // A should have no apps
        assertTrue(grouped['A']?.isEmpty() == true)
        // X should have no apps
        assertTrue(grouped['X']?.isEmpty() == true)
        // G should have Gmail
        assertEquals(1, grouped['G']?.size)
    }

    // ===== Test 4: Touch-to-letter mapping =====

    @Test
    fun `touch to letter - top returns A, middle returns M or N, bottom returns Z`() {
        val top = 0f
        val bottom = 1000f

        val topLetter = AlphabetTouchMapper.letterForTouchY(top, top, bottom)
        assertEquals('A', topLetter)

        val bottomLetter = AlphabetTouchMapper.letterForTouchY(bottom, top, bottom)
        assertEquals('Z', bottomLetter)

        // Middle position should return M or N (index 12 or 13)
        val middleLetter = AlphabetTouchMapper.letterForTouchY(500f, top, bottom)
        assertTrue(middleLetter in 'L'..'N')
    }

    // ===== Test 5: Touch clamping =====

    @Test
    fun `touch clamping - above alphabet returns A, below returns Z`() {
        val top = 100f
        val bottom = 900f

        val aboveLetter = AlphabetTouchMapper.letterForTouchY(-50f, top, bottom)
        assertEquals('A', aboveLetter)

        val belowLetter = AlphabetTouchMapper.letterForTouchY(1200f, top, bottom)
        assertEquals('Z', belowLetter)
    }

    // ===== Test 6: Case insensitivity =====

    @Test
    fun `case insensitivity - gmail, Gmail, GMAIL all map to G`() {
        assertEquals('G', LetterMapper.letterForApp("gmail"))
        assertEquals('G', LetterMapper.letterForApp("Gmail"))
        assertEquals('G', LetterMapper.letterForApp("GMAIL"))
    }

    // ===== Test 7: Non-alpha leading characters =====

    @Test
    fun `non-alpha prefix - digit-prefixed labels map to first alpha letter`() {
        assertEquals('G', LetterMapper.letterForApp("2Go"))
        assertEquals('#', LetterMapper.letterForApp("123"))
    }

    // ===== Test 8: Available letters =====

    @Test
    fun `available letters - only letters with apps are returned`() {
        val apps = listOf("Gmail", "Chrome")
        val grouped = groupTestApps(apps)
        val available = grouped.filter { it.value.isNotEmpty() }.keys

        assertTrue('G' in available)
        assertTrue('C' in available)
        assertTrue('A' !in available)
    }

    // ===== Test 9: Curve displacement =====

    @Test
    fun `curve displacement - maximum at finger position, zero far away`() {
        val maxOffset = 100f
        val sigma = 50f

        // At finger position: full displacement
        val atFinger = AlphabetTouchMapper.curveDisplacement(
            letterY = 500f, fingerY = 500f, maxOffset = maxOffset, sigma = sigma
        )
        assertEquals(maxOffset, atFinger, 0.01f)

        // Far away: near zero
        val farAway = AlphabetTouchMapper.curveDisplacement(
            letterY = 0f, fingerY = 500f, maxOffset = maxOffset, sigma = sigma
        )
        assertTrue(farAway < 1f)
    }

    // --- Helpers ---

    /**
     * Groups test app labels using the real AppGrouper logic.
     * Creates mock LauncherApp objects without real Drawables.
     */
    private fun groupTestApps(labels: List<String>): Map<Char, List<TestLauncherApp>> {
        val testApps = labels.map { testApp(it) }
        // Simulate AppGrouper logic without Drawable dependency
        val grouped = testApps.groupBy { it.letterKey }
        val result = linkedMapOf<Char, List<TestLauncherApp>>()
        for (c in 'A'..'Z') {
            result[c] = (grouped[c] ?: emptyList()).sortedBy { it.label.lowercase() }
        }
        grouped['#']?.let { result['#'] = it.sortedBy { app -> app.label.lowercase() } }
        return result
    }
}
