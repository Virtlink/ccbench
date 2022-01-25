package mb.spree.green

import java.util.*

/** A string term. */
class StringTerm(
    /** The token that gives this term its value. */
    val token: Token,
    /** Annotations for this term. */
    override val annotations: List<Term> = emptyList(),
): Term() {
    /** The string value. */
    val value: String get() = token.content

    constructor(value: String, annotations: List<Term> = emptyList())
            : this(Token(value, TextElementKind.NUMERIC_CONSTANT), annotations)

    override val subterms: List<Term>
        get() = emptyList()
    override val children: List<Node>
        get() = listOf(token)

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitStringTerm(this)

    override fun <A0, R> accept(visitor: TermVisitor1<A0, R>, arg0: A0): R = visitor.visitStringTerm(this, arg0)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.token,
        this.annotations,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? StringTerm ?: return false
        if (this.hashCode != that.hashCode) return false
        return this.token == that.token
            && this.annotations == that.annotations
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}