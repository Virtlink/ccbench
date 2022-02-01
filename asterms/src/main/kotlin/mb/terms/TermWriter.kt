package mb.terms

/**
 * Writes the terms in a tree.
 */
class TermWriter(
    private val buffer: Appendable
): TermVisitor<Unit> {
    override fun visitTerm(term: Term) {
        throw IllegalStateException("Unknown type of term: ${term::class.java}")
    }

    override fun visitApplTerm(term: ApplTerm) {
        buffer.append(term.name)
        appendAllOf(term.subterms, "(", ")")
        appendAnnotations(term)
    }

    override fun visitListTerm(term: ListTerm) {
        appendAllOf(term.subterms, "[", "]")
        appendAnnotations(term)
    }

    override fun visitAmbTerm(term: AmbTerm) {
        appendAllOf(term.subterms, "<", ">")
    }

    override fun visitStringTerm(term: StringTerm) {
        buffer.append(term.value)
        appendAnnotations(term)
    }

    override fun visitIntTerm(term: IntTerm) {
        buffer.append(term.value.toString())
        appendAnnotations(term)
    }

    override fun visitTermVar(term: TermVar) {
        buffer.append('$')
        buffer.append(term.name)
        buffer.append('$')
        appendAnnotations(term)
    }

    private fun appendAnnotations(term: Term) {
        appendAllOf(term.annotations, "{", "}")
    }

    private fun appendAllOf(terms: Iterable<Term>, start: String, end: String, separator: String = ", ", skipIfEmpty: Boolean = true) {
        val iterator = terms.iterator()
        if (!iterator.hasNext()) {
            if (skipIfEmpty) return
            buffer.append(start)
            buffer.append(end)
            return
        }

        buffer.append(start)
        iterator.next().accept(this)
        while (iterator.hasNext()) {
            buffer.append(separator)
            iterator.next().accept(this)
        }
        buffer.append(end)
    }

    override fun visitValueTerm(term: ValueTerm<*>) {
        throw IllegalStateException("Unknown type of value term: ${term::class.java}")
    }
}
