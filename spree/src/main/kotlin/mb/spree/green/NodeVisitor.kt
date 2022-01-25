package mb.spree.green

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
    fun visitStringTerm(term: StringTerm): R
    fun visitIntTerm(term: IntTerm): R
    fun visitTermVar(term: TermVar): R
}

/**
 * Visits nodes in a tree.
 */
interface NodeVisitor1<in A0, out R>: TermVisitor1<A0, R> {
    fun visitNode(node: Node, arg0: A0): R = throw UnsupportedOperationException("This node type is not supported: ${node::class.java.name}")
    fun visitToken(token: Token, arg0: A0): R
}

/**
 * Visits terms in a tree.
 */
interface TermVisitor1<in A0, out R> {
    fun visitTerm(term: Term, arg0: A0): R = throw UnsupportedOperationException("This term type is not supported: ${term::class.java.name}")
    fun visitApplTerm(term: ApplTerm, arg0: A0): R
    fun visitAmbTerm(term: AmbTerm, arg0: A0): R
    fun visitListTerm(term: ListTerm, arg0: A0): R
    fun visitStringTerm(term: StringTerm, arg0: A0): R
    fun visitIntTerm(term: IntTerm, arg0: A0): R
    fun visitTermVar(term: TermVar, arg0: A0): R
}