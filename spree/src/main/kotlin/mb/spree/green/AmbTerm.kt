package mb.spree.green

import java.util.*

/**
 * An ambiguity term.
 */
class AmbTerm(
    /** The alternatives. */
    override val subterms: List<Term>
): Term() {
    override val children: List<Term> get() = subterms

    override val annotations: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAmbTerm(this)

    override fun <A0, R> accept(visitor: TermVisitor1<A0, R>, arg0: A0): R = visitor.visitAmbTerm(this, arg0)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.subterms,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? AmbTerm ?: return false
        if (this.hashCode != that.hashCode) return false
        return this.subterms == that.subterms
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}