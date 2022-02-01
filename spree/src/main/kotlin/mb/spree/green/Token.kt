package mb.spree.green

/**
 * Semantic syntax, such as literals, identifiers, operators, and keywords.
 */
class Token(
    /** The text content of the token, excluding leading or trailing layout. */
    // TODO: In which cases can this be an empty string? Only for BOF/EOF?
    override val content: String,
    /** The kind of token. */
    override val kind: TextElementKind,
    /** The layout elements preceding the token. */
    val leadingLayout: List<Layout> = emptyList(),
    /** The layout elements following the token. */
    val trailingLayout: List<Layout> = emptyList(),
): Node, TextElement {
    override val isLayout: Boolean get() = false

    override val children: List<Node>
        get() = emptyList()
    override val allTextElements: Sequence<TextElement>
        get() = sequence {
            yieldAll(leadingLayout)
            yield(this@Token)
            yieldAll(trailingLayout)
        }

    override fun text(): String = super<TextElement>.text()

    override fun <R> accept(visitor: NodeVisitor<R>): R = visitor.visitToken(this)

    override fun <A0, R> accept(visitor: NodeVisitor1<A0, R>, arg0: A0): R = visitor.visitToken(this, arg0)

    companion object {
        /** A special token indicating the start of the input. */
        val BOF: Token = Token("", TextElementKind.BOF)
        /** A special token indicating the end of the input. */
        val EOF: Token = Token("", TextElementKind.EOF)
    }
}