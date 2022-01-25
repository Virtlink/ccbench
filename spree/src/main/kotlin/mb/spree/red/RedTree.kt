package mb.spree.red

/**
 * A concrete syntax tree.
 */
class RedTree {

    /** The root of the tree. */
    val root: RedTerm by lazy { TODO() }

    /** Computes the text of this tree, including leading and trailing layout. */
    fun text(): String = root.text()

}