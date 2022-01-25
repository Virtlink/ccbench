package mb.spree.green

/**
 * A term in an abstract syntax tree.
 */
abstract class Term: Node {
    /** The direct subterms of this term. */
    abstract val subterms: List<Term>

    /** Annotations for this term. */
    abstract val annotations: List<Term>

    /**
     * Accepts a visitor.
     *
     * @param visitor the visitor to accept
     * @return the result returned by the visitor
     */
    abstract fun <R> accept(visitor: TermVisitor<R>): R

    /**
     * Accepts a visitor.
     *
     * @param visitor the visitor to accept
     * @param arg0 the argument to the visitor
     * @return the result returned by the visitor
     */
    abstract fun <A0, R> accept(visitor: TermVisitor1<A0, R>, arg0: A0): R

    final override fun <R> accept(visitor: NodeVisitor<R>): R = accept(visitor as TermVisitor<R>)

    final override fun <A0, R> accept(visitor: NodeVisitor1<A0, R>, arg0: A0): R = accept(visitor as TermVisitor1<A0, R>, arg0)

    /**
     * Resolves a path starting from this term.
     *
     * @param path the path to resolve
     * @return the resulting term
     * @throws IndexOutOfBoundsException the path is invalid
     */
    fun resolve(path: TreePath<Term>): Term {
        if (path.tail == null) return this
        val subterm = this.subterms[path.index]
        return subterm.resolve(path.tail)
    }

    abstract override fun toString(): String
}