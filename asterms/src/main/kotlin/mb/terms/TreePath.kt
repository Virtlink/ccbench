package mb.terms

/**
 * A term path.
 */
class TreePath<T> {
    val index: Int
    val tail: TreePath<T>?

    constructor(index: Int, tail: TreePath<T>) {
        this.index = index
        this.tail = tail
    }

    private constructor() {
        index = 0
        tail = null
    }

    companion object {
        private val EMPTY = TreePath<Nothing>()

        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): TreePath<T> {
            return EMPTY as TreePath<T>
        }
    }

    val isEmpty get() = tail == null

    /**
     * Prepends the given index to the path.
     */
    fun prepend(index: Int): TreePath<T> {
        return TreePath(index, this)
    }
}
