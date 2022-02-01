package mb.terms


/**
 * Visits nodes in a tree.
 */
interface NodeVisitor<out R>: TermVisitor<R> {
    fun visitNode(node: Node): R = throw UnsupportedOperationException("This node type is not supported: ${node::class.java.name}")
    fun visitToken(token: Token): R
}

/**
 * Visits terms in a tree.
 */
interface TermVisitor<out R> {
    fun visitTerm(term: Term): R = throw UnsupportedOperationException("This term type is not supported: ${term::class.java.name}")
    fun visitApplTerm(term: ApplTerm): R
    fun visitListTerm(term: ListTerm): R
    fun visitAmbTerm(term: AmbTerm): R
    fun visitValueTerm(term: ValueTerm<*>): R
    fun visitStringTerm(term: StringTerm): R
    fun visitIntTerm(term: IntTerm): R
    fun visitTermVar(term: TermVar): R
}
