package mb.spree.red

import mb.spree.Location
import mb.spree.Range
import mb.spree.green.*

/**
 * A term in an abstract syntax tree.
 */
abstract class RedTerm(
    /** The direct parent of this term; or `null` if it is the root term. */
    val parent: RedTerm?,
    /** The zero-based index of this term among the parent's children; or 0 if it is the root term. */
    val index: Int,
): RedNode {
    override val innerNode: Node
        get() = innerTerm
    /** The underlying term. */
    abstract val innerTerm: Term
    /** The root of the tree; or this term if it is the root term. */
    val root: RedNode get() = parent?.root ?: this

    /** The direct subterms of this term. */
    abstract val subterms: List<RedTerm>
    /** Annotations for this term. */
    abstract val annotations: Map<RedTerm, RedTerm>
}


class RedApplTerm(
    /** The direct parent of this term; or `null` if it is the root term. */
    parent: RedTerm?,
    /** The zero-based index of this term among the parent's children; or 0 if it is the root term. */
    index: Int,
    /** The absolute location of the term. */
    start: Location,
    override val innerTerm: ApplTerm,
): RedTerm(parent, index) {

    override val annotations: Map<RedTerm, RedTerm> = TODO() //LazyRedTermMap(this, innerTerm.annotations)
    override val children: List<RedNode> = LazyRedNodeList(this, innerTerm.children)
    override val subterms: List<RedTerm> = RedTermListView(this.children)

    override val range: Range
        get() = TODO("Not yet implemented")
}