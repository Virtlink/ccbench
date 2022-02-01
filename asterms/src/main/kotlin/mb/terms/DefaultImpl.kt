package mb.terms

import java.util.*

/**
 * The default node factory.
 */
class DefaultNodeFactory(): NodeFactory {

    override fun createApplTerm(
        name: String,
        children: List<Node>,
        annotations: List<Term>
    ): ApplTerm {
        return ApplTermImpl(name, children, annotations)
    }

    override fun createStringTerm(
        // FIXME: Should there be three tokens?
        prefixToken: Token,
        valueToken: Token,
        suffixToken: Token,
        annotations: List<Term>
    ): StringTerm {
        return StringTermImpl(prefixToken, valueToken, suffixToken, annotations)
    }

    override fun createIntTerm(
        // FIXME: Should there be three tokens?
        prefixToken: Token,
        valueToken: Token,
        suffixToken: Token,
        annotations: List<Term>
    ): IntTerm {
        return IntTermImpl(prefixToken, valueToken, suffixToken, annotations)
    }

    override fun createTermVar(
        // FIXME: Should there be three tokens?
        prefixToken: Token,
        nameToken: Token,
        suffixToken: Token,
        annotations: List<Term>
    ): TermVar {
        return TermVarImpl(prefixToken, nameToken, suffixToken, annotations)
    }

    override fun createListTerm(
        elements: List<Term>
    ): ListTerm {
        return ListTermImpl(elements)
    }

    override fun createAmbTerm(
        alternatives: List<Term>
    ): AmbTerm {
        return AmbTermImpl(alternatives)
    }

    override fun createToken(
        content: String,
        kind: TextElementKind,
        isConcrete: Boolean,
        leadingLayout: List<Layout>,
        trailingLayout: List<Layout>
    ): Token {
        return TokenImpl(content, kind, isConcrete, leadingLayout, trailingLayout)
    }

    override fun createLayout(
        content: String,
        kind: TextElementKind,
        isConcrete: Boolean
    ): Layout {
        return LayoutImpl(content, kind, isConcrete)
    }
}


/**
 * A term (non-leaf node) in an ASTerm tree.
 */
private abstract class TermImpl: Term {
    abstract override val children: List<Node>
    abstract override val subterms: List<Term>
    abstract override val annotations: List<Term>

    override fun resolve(path: TreePath<Term>): Term {
        if (path.tail == null) return this
        val subterm = this.subterms[path.index]
        return subterm.resolve(path.tail)
    }

    abstract override fun <R> accept(visitor: TermVisitor<R>): R

    final override fun <R> accept(visitor: NodeVisitor<R>): R = accept(visitor as TermVisitor<R>)

    override val allTokens: Sequence<Token> get() = sequence {
        yieldAll(children.flatMap { it.allTokens })
    }
    override val allTextElements: Sequence<TextElement> get() = sequence {
        yieldAll(children.flatMap { it.allTextElements })
    }

    override fun <A : Appendable> writeTo(buffer: A, concrete: Boolean): A {
        for (textElement in allTextElements) {
            if (concrete && !textElement.isConcrete) continue
            buffer.append(textElement.content)
        }
        return buffer
    }
}

/**
 * A constructor application term.
 */
private class ApplTermImpl(
    override val name: String,
    override val children: List<Node>,
    override val annotations: List<Term>
): ApplTerm, TermImpl() {

    init {
        require(children.isNotEmpty()) { "This term must have at least one child." }
        require(name.isEmpty() || name.isNotBlank()) { "The constructor name must not be blank." }
    }

    override val subterms: List<Term> = children.filterIsInstance<Term>()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitApplTerm(this)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int = hashCode

    private fun computeHashCode(): Int = Objects.hash(
        this.name,
        this.children,
        this.annotations,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? ApplTermImpl ?: return false
        if (this.hashCode != that.hashCode) return false
        // @formatter:off
        return this.name == that.name
            && this.children == that.children
            && this.annotations == that.annotations
        // @formatter:on
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}

/**
 * A value term that computes its value when requested for the first time.
 */
private abstract class ValueTermImpl<T>: TermImpl(), ValueTerm<T> {

    override val value: T by lazy { computeValueFromTokens() }

    /**
     * Computes the value of this term from its tokens.
     *
     * @return the computed value
     */
    protected abstract fun computeValueFromTokens(): T

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitValueTerm(this)
}

/**
 * A string term.
 */
private class StringTermImpl(
    /** The token before the value. */
    val prefixToken: Token,
    /** The token with the value. */
    val valueToken: Token,
    /** The token after the value. */
    val suffixToken: Token,
    override val annotations: List<Term>
): StringTerm, ValueTermImpl<String>() {

    override val children: List<Node> get() = listOf(prefixToken, valueToken, suffixToken)
    override val subterms: List<Term> get() = emptyList()

    override val value: String get() = computeValueFromTokens()
    override fun computeValueFromTokens(): String = valueToken.content

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitStringTerm(this)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.prefixToken,
        this.valueToken,
        this.suffixToken,
        this.annotations,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? StringTermImpl ?: return false
        if (this.hashCode != that.hashCode) return false
        // @formatter:off
        return this.prefixToken == that.prefixToken
            && this.valueToken == that.valueToken
            && this.suffixToken == that.suffixToken
            && this.annotations == that.annotations
        // @formatter:on
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}

/**
 * An integer term.
 */
private class IntTermImpl(
    /** The token before the value. */
    val prefixToken: Token,
    /** The token with the value. */
    val valueToken: Token,
    /** The token after the value. */
    val suffixToken: Token,
    override val annotations: List<Term>
): IntTerm, ValueTermImpl<Int>() {

    override val children: List<Node> get() = listOf(prefixToken, valueToken, suffixToken)
    override val subterms: List<Term> get() = emptyList()

    override val value: Int get() = computeValueFromTokens()
    override fun computeValueFromTokens(): Int = valueToken.content.toInt()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitIntTerm(this)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.prefixToken,
        this.valueToken,
        this.suffixToken,
        this.annotations,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? IntTermImpl ?: return false
        if (this.hashCode != that.hashCode) return false
        // @formatter:off
        return this.prefixToken == that.prefixToken
            && this.valueToken == that.valueToken
            && this.suffixToken == that.suffixToken
            && this.annotations == that.annotations
        // @formatter:on
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}

/**
 * A term variable.
 */
private class TermVarImpl(
    /** The token before the variable name. */
    val prefixToken: Token,
    /** The token with the variable name. */
    val nameToken: Token,
    /** The token after the variable name. */
    val suffixToken: Token,
    override val annotations: List<Term>
): TermVar, TermImpl() {

    override val name: String get() = nameToken.content

    override val children: List<Node> get() = listOf(prefixToken, nameToken, suffixToken)
    override val subterms: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitTermVar(this)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.prefixToken,
        this.nameToken,
        this.suffixToken,
        this.annotations,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? TermVarImpl ?: return false
        if (this.hashCode != that.hashCode) return false
        // @formatter:off
        return this.prefixToken == that.prefixToken
            && this.nameToken == that.nameToken
            && this.suffixToken == that.suffixToken
            && this.annotations == that.annotations
        // @formatter:on
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}



/**
 * A list term.
 */
private class ListTermImpl(
    override val children: List<Node>
): ListTerm, TermImpl() {

    init {
        require(children.isNotEmpty()) { "This term must have at least one child." }
    }

    override val elements: List<Term> get() = subterms
    override val subterms: List<Term> get() = children.filterIsInstance<Term>()
    override val annotations: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitListTerm(this)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.subterms,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? ListTermImpl ?: return false
        if (this.hashCode != that.hashCode) return false
        // @formatter:off
        return this.children == that.children
        // @formatter:on
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}

/**
 * An ambiguous term.
 */
private class AmbTermImpl(
    override val alternatives: List<Term>
): AmbTerm, TermImpl() {

    init {
        require(alternatives.size >= 2) { "An ambiguous term must have at least two alternatives." }
    }

    override val subterms: List<Term> get() = alternatives
    override val children: List<Term> get() = alternatives
    override val annotations: List<Term> get() = emptyList()

    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAmbTerm(this)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.subterms,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? AmbTermImpl ?: return false
        if (this.hashCode != that.hashCode) return false
        // @formatter:off
        return this.alternatives == that.alternatives
        // @formatter:on
    }

    override fun toString(): String = StringBuilder().apply { accept(TermWriter(this)) }.toString()
}

/**
 * A token (leaf node) text element in an ASTerm tree.
 */
private class TokenImpl(
    /** The text content of the text element, excluding leading or trailing layout; or an empty string. */
    override val content: String,
    /** The kind of token. */
    override val kind: TextElementKind,
    /** Whether the token actually appears in the source. */
    override val isConcrete: Boolean = true,
    /** The layout elements preceding the token. */
    override val leadingLayout: List<Layout> = emptyList(),
    /** The layout elements following the token. */
    override val trailingLayout: List<Layout> = emptyList(),
): Token {
    override val children: List<Node> get() = emptyList()

    override val allTokens: Sequence<Token> get() = sequenceOf(this)
    override val allTextElements: Sequence<TextElement> get() = sequence {
        yieldAll(leadingLayout)
        yield(this@TokenImpl)
        yieldAll(trailingLayout)
    }

    override fun <A : Appendable> writeTo(buffer: A, concrete: Boolean): A {
        for (textElement in allTextElements) {
            if (concrete && !textElement.isConcrete) continue
            buffer.append(textElement.content)
        }
        return buffer
    }

    override fun <R> accept(visitor: NodeVisitor<R>): R = visitor.visitToken(this)

    private val hashCode: Int = computeHashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    private fun computeHashCode(): Int = Objects.hash(
        this.content,
        this.kind,
        this.isConcrete,
        this.leadingLayout,
        this.trailingLayout,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? TokenImpl ?: return false
        if (this.hashCode != that.hashCode) return false
        // @formatter:off
        return this.content == that.content
            && this.kind == that.kind
            && this.isConcrete == that.isConcrete
            && this.leadingLayout == that.leadingLayout
            && this.trailingLayout == that.trailingLayout
        // @formatter:on
    }

    override fun toString(): String = content
}

/**
 * A layout text element.
 */
private class LayoutImpl(
    /** The text content of the text element; or an empty string. */
    override val content: String,
    /** The kind of token. */
    override val kind: TextElementKind,
    /** Whether the token actually appears in the source. */
    override val isConcrete: Boolean = true,
): Layout {

    override fun hashCode(): Int {
        return Objects.hash(
            this.content,
            this.kind,
            this.isConcrete,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? LayoutImpl ?: return false
        // @formatter:off
        return this.content == that.content
            && this.kind == that.kind
            && this.isConcrete == that.isConcrete
        // @formatter:on
    }

    override fun toString(): String = content
}

