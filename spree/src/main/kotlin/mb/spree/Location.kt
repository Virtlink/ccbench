package mb.spree

import kotlinx.serialization.*

/**
 * An absolute location in the source text.
 *
 * Note that the character offset is based on the number of characters, not the number of columns.
 * For example, after a tab character, the character offset will be incremented by 1,
 * regardless of how many spaces are used to render the tab character.
 */
@Serializable
data class Location(
    /** The zero-based character offset from the start of the text. */
    val offset: Long,
    /** The one-based line number from the start of the text. */
    val line: Int,
    /** The one-based character offset from the start of the line. */
    val character: Int,
): Comparable<Location> {

    init {
        require(offset >= 0) { "Offset must be greater than or equal to 0." }
        require(line >= 1) { "Line number must be greater than or equal to 1." }
        require(character >= 1) { "Character offset must be greater than or equal to 1." }
    }

    /**
     * Compares this instance to the specified instance.
     *
     * Only compare locations from the same source text and version.
     * Comparing locations from different source texts or source text versions makes no sense.
     */
    override fun compareTo(other: Location): Int {
        return this.offset.compareTo(other.offset)
    }

    override fun toString(): String {
        return "$line:$character@$offset"
    }
}
