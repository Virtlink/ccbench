package mb.spree.green

/**
 * The kind of text element, as a 'scope name'.
 *
 * For example, `keyword.control.java` is a Java control keyword such as `return` and `continue`;
 * whereas `comment.block.documentation.kotlin` is a Kotlin documentation comment.
 */
@JvmInline
value class TextElementKind(val value: String) {
    companion object {
        // Pre-defined kinds
        val STRING = TextElementKind("string")
        val NUMERIC_CONSTANT = TextElementKind("constant.numeric")
    }
}

