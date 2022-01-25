package mb.spree.green

import java.util.*

/**
 * A constructor application term.
 */
class ApplTerm(
    /** The constructor name; or an empty string for a tuple. */
    val name: String,
    /** The direct children of this term (both terms and tokens). */
    override val children: List<Node>,
    /** Annotations for this term. */
    override val annotations: List<Term> = emptyList(),
): Term() {

    override val subterms: List<Term> get() = TermListView(this.children)

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitApplTerm(this)

    override fun <A0, R> accept(visitor: TermVisitor1<A0, R>, arg0: A0): R = visitor.visitApplTerm(this, arg0)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.name,
        this.children,
        this.annotations,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? ApplTerm ?: return false
        if (this.hashCode != that.hashCode) return false
        return this.name == that.name
            && this.children == that.children
            && this.annotations == that.annotations
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}