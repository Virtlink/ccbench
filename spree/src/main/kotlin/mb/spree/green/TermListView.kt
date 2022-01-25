package mb.spree.green

/**
 * A term list view of a node list.
 */
class TermListView(
    private val list: List<Node>,
) : AbstractList<Term>() {
    /** An array of indices in the list of red nodes. */
    private val termIndices: IntArray = list.mapIndexedNotNull { i, node -> if (node is Term) i else null }.toIntArray()

    override val size: Int
        get() = termIndices.size

    override fun get(index: Int): Term {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index must be between 0 and $size (exclusive), got: $index")
        return list[termIndices[index]] as Term
    }
}