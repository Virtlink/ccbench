package mb.terms

/**
 * An ASTerm factory.
 */
interface NodeFactory {

    /**
     * Creates a constructor application term with the given name, children, and annotations.
     *
     * @param name the name of the constructor
     * @param children the children of the constructor application, which must not be empty
     * @param annotations the annotations of the term
     * @return the built constructor application term
     */
    fun createApplTerm(
        name: String,
        children: List<Node>,
        annotations: List<Term>
    ): ApplTerm

    /**
     * Creates a string term with the given tokens and annotations.
     *
     * @param prefixToken the token before the value
     * @param valueToken the token with the value
     * @param suffixToken the token after the value
     * @param annotations the annotations of the term
     * @return the built string term
     */
    fun createStringTerm(
        prefixToken: Token,
        valueToken: Token,
        suffixToken: Token,
        annotations: List<Term>
    ): StringTerm

    /**
     * Creates an integer term with the given tokens and annotations.
     *
     * @param prefixToken the token before the value
     * @param valueToken the token with the value
     * @param suffixToken the token after the value
     * @param annotations the annotations of the term
     * @return the built integer term
     */
    fun createIntTerm(
        prefixToken: Token,
        valueToken: Token,
        suffixToken: Token,
        annotations: List<Term>
    ): IntTerm

    /**
     * Creates a term variable with the given tokens and annotations.
     *
     * @param prefixToken the token before the name
     * @param nameToken the token with the name
     * @param suffixToken the token after the name
     * @param annotations the annotations of the term
     * @return the built term variable
     */
    fun createTermVar(
        prefixToken: Token,
        nameToken: Token,
        suffixToken: Token,
        annotations: List<Term>
    ): TermVar

    fun createListTerm(
        elements: List<Term>
    ): ListTerm

    fun createAmbTerm(
        alternatives: List<Term>
    ): AmbTerm

    fun createToken(
        content: String,
        kind: TextElementKind,
        isConcrete: Boolean,
        leadingLayout: List<Layout>,
        trailingLayout: List<Layout>
    ): Token

    fun createLayout(
        content: String,
        kind: TextElementKind,
        isConcrete: Boolean,
    ): Layout
}
