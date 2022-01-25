package mb.spree.green

import java.util.*

/** A list term. */
class ListTerm(
    /** The direct children of this term (both terms and tokens). */
    override val children: List<Node>,
): Term() {
    override val subterms: List<Term>
        get() = children.filterIsInstance<Term>()
    override val annotations: List<Term>
        get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitListTerm(this)

    override fun <A0, R> accept(visitor: TermVisitor1<A0, R>, arg0: A0): R = visitor.visitListTerm(this, arg0)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.children,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? ListTerm ?: return false
        if (this.hashCode != that.hashCode) return false
        return this.children == that.children
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}