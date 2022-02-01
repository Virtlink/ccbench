package mb.terms

/**
 * A node in an ASTerm tree.
 */
interface Node {
    /** The children of this node. */
    val children: List<Node>

    /** A lazy sequence of all tokens in this tree. */
    val allTokens: Sequence<Token>
    /** A lazy sequence of all text elements in this tree. */
    val allTextElements: Sequence<TextElement>

    /**
     * Computes the textual representation of this tree, including layout.
     *
     * Nothing will be returned if [concrete] is `true` but the tree is not concrete.
     *
     * @param concrete `true` to include only concrete text elements
     * @return the textual representation of this tree
     */
    fun text(concrete: Boolean = false): String =
        writeTo(StringBuilder(), concrete).toString()

    /**
     * Writes the textual representation of this tree, including layout, to the given [buffer].
     *
     * Nothing will be written if [concrete] is `true` but the tree is not concrete.
     *
     * @param buffer the buffer to write to
     * @param concrete `true` to include only concrete text elements
     * @return the buffer itself
     */
    fun <A: Appendable> writeTo(buffer: A, concrete: Boolean = false): A

    /**
     * Accepts a node visitor, visiting all terms and tokens in this tree.
     *
     * @param visitor the visitor to accept
     * @return the result returned by the visitor
     */
    fun <R> accept(visitor: NodeVisitor<R>): R
}

/**
 * A term (non-leaf node) in an ASTerm tree.
 */
interface Term: Node {
    /** The subterms of this term. This is a subset of the children of this term. */
    val subterms: List<Term>
    /** The annotations of this term. */
    val annotations: List<Term>

    /**
     * Resolves a path to a subterm.
     *
     * @param path the path to resolve
     * @return the subterm at the end of the path
     */
    fun resolve(path: TreePath<Term>): Term

    /**
     * Accepts a term visitor, visiting all terms in this tree.
     *
     * @param visitor the visitor to accept
     * @return the result returned by the visitor
     */
    fun <R> accept(visitor: TermVisitor<R>): R
}

/**
 * A constructor application term.
 */
interface ApplTerm: Term {
    /** The name of the constructor. */
    val name: String
    /** Whether this is a tuple term. */
    val isTuple: Boolean get() = name.isEmpty()
}

/**
 * A value term.
 */
interface ValueTerm<T>: Term {
    /** The value of this term. */
    val value: T
}

/**
 * A string term.
 */
interface StringTerm: ValueTerm<String> {
    /** The string value of this term. */
    override val value: String
}

/**
 * An integer term.
 */
interface IntTerm: ValueTerm<Int> {
    /** The integer value of this term. */
    override val value: Int
}

/**
 * A term variable.
 */
interface TermVar: Term {
    /** The name of this variable. */
    val name: String
}

/**
 * A list term.
 */
interface ListTerm: Term {
    /** The elements in this list. This is the same as the children of the list. */
    val elements: List<Term>
}

/**
 * An ambiguous term.
 */
interface AmbTerm: Term {
    /** The alternatives of this ambiguous term. This is the same as the children of the term. */
    val alternatives: List<Term>
}

/**
 * A text element, such as a token or layout.
 */
interface TextElement {
    /** The text content of the element. */
    val content: String
    /** The kind of content of the element. */
    val kind: TextElementKind
    /** Whether the text element actually appears in the source. */
    val isConcrete: Boolean
}

/**
 * A token (leaf node) text element in an ASTerm tree.
 */
interface Token: Node, TextElement {
    /** The leading layout that belongs to this token. */
    val leadingLayout: List<Layout>
    /** The trailing layout that belongs to this token. */
    val trailingLayout: List<Layout>
}

/**
 * A layout text element.
 *
 * Layout is not part of the ASTerm tree
 * but can be found through a token's leading or trailing layout.
 */
interface Layout: TextElement {
}
