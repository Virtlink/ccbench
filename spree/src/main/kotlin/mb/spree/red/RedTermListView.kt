package mb.spree.red

/**
 * A red term list view of a red node list.
 */
class RedTermListView(
    private val list: List<RedNode>,
) : AbstractList<RedTerm>() {
    /** An array of indices in the list of red nodes. */
    private val termIndices: IntArray = list.mapIndexedNotNull { i, node -> if (node is RedTerm) i else null }.toIntArray()

    override val size: Int
        get() = termIndices.size

    override fun get(index: Int): RedTerm {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index must be between 0 and $size (exclusive), got: $index")
        return list[termIndices[index]] as RedTerm
    }

}