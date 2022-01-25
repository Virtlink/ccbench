package mb.spree.green

/**
 * A node in a concrete syntax tree.
 */
sealed interface Node {
    /** The direct children of this node (both terms and tokens). */
    val children: List<Node>

    /** A lazy sequence with all tokens that constitute this node. */
    val allTokens: Sequence<Token> get() = sequence {
        yieldAll(children.flatMap { it.allTokens })
    }
    /** A lazy sequence with all text elements that constitute this node. */
    val allTextElements: Sequence<TextElement> get() = sequence {
        yieldAll(children.flatMap { it.allTextElements })
    }

    /** Computes the text of this node, including leading and trailing layout. */
    fun text(): String = allTextElements.joinToString("")

    /**
     * Accepts a visitor.
     *
     * @param visitor the visitor to accept
     * @return the result returned by the visitor
     */
    fun <R> accept(visitor: NodeVisitor<R>): R

    /**
     * Accepts a visitor.
     *
     * @param visitor the visitor to accept
     * @param arg0 the argument to the visitor
     * @return the result returned by the visitor
     */
    fun <A0, R> accept(visitor: NodeVisitor1<A0, R>, arg0: A0): R
}