package com.jyotirmay.minimallauncher.domain

/**
 * Maps an app label to its grouping letter (A–Z).
 * Non-alphabetic leading characters map to '#'.
 */
object LetterMapper {

    /**
     * Returns the first alphabetic character of [label] uppercased,
     * or '#' if the label starts with a digit/symbol.
     */
    fun letterForApp(label: String): Char {
        val first = label.firstOrNull { it.isLetter() }
        return first?.uppercaseChar() ?: '#'
    }
}
