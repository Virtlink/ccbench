package mb.spree.green

interface TextElement {
    /** The content of the text element, excluding leading or trailing layout. */
    val content: String
    /** The kind of text element. */
    val kind: TextElementKind
    /** Whether this is a layout element or a token element. */
    val isLayout: Boolean

    /** A lazy sequence with all text elements that constitute this node. */
    val allTextElements: Sequence<TextElement>
    /** Computes the text of this node, including leading and trailing layout. */
    fun text(): String = allTextElements.joinToString("")
}