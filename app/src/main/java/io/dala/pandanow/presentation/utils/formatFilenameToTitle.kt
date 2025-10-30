package io.dala.pandanow.presentation.utils

/**
 * Converts a filename to a readable title by:
 * - Replacing underscores and hyphens with spaces
 * - Capitalizing the first letter of each word
 * - Removing file extensions
 * - Cleaning up extra spaces
 */
fun formatFilenameToTitle(filename: String): String {
    return filename
        .replace("_", " ")
        .replace("-", " ")
        .replace(".", " ")
        .split(" ")
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}