package mb.spree.green

/**
 * Non-semantic syntax, such as whitespace, comments, and newlines.
 *
 * Upon parsing, sequences of layout elements are associated with tokens.
 * Layout on the start of a line is associated with the first token on that line.
 * Layout after a token is associated with that token up to the next token on the same line, or the end of the line.
 * Layout at the start of the file before the first token is associated with the first token.
 * Layout at the end of the file after the last token is associated with the last token.
 */
class Layout(
    /** The text content of the layout. */
    override val content: String,
    /** The kind of layout. */
    override val kind: TextElementKind,
): TextElement {
    override val isLayout: Boolean get() = true

    override val allTextElements: Sequence<TextElement>
        get() = sequenceOf(this)
}