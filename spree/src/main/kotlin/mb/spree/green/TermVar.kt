package mb.spree.green

import java.util.*

/** A term variable. */
class TermVar(
    /** The name of the variable. */
    val name: String,
): Term() {
    override val subterms: List<Term>
        get() = emptyList()
    override val children: List<Node>
        get() = emptyList()
    override val annotations: List<Term>
        get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitTermVar(this)

    override fun <A0, R> accept(visitor: TermVisitor1<A0, R>, arg0: A0): R = visitor.visitTermVar(this, arg0)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.name,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? TermVar ?: return false
        if (this.hashCode != that.hashCode) return false
        return this.name == that.name
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}