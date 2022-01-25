package mb.spree

/**
 * An absolute range in a source text.
 *
 * Only use a start and end location from the same source text and version.
 * Start and end locations from different source texts or source text versions makes no sense.
 */
data class Range(
    /** The inclusive start of the range. */
    val start: Location,
    /** The exclusive end of the range. */
    val end: Location,
) {
    /** Whether the range is empty. */
    val isEmpty: Boolean get() = length == 0L

    /** The length of the range; i.e. the number of characters between the start and end locations. */
    val length: Long
        get() = end.offset - start.offset

    /** The inclusive start offset of the range. */
    val startOffset: Long
        get() = start.offset

    /** The exclusive end offset of the range. */
    val endOffset: Long
        get() = end.offset

    override fun toString(): String {
        return if (!isEmpty) "$start..$end"
        else "$start"
    }
}