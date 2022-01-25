package mb.spree.red

import mb.spree.Range
import mb.spree.green.Node

/**
 * A node in a concrete syntax tree.
 */
sealed interface RedNode {
    /** The underlying node. */
    val innerNode: Node
    /** The direct children of this node (both terms and tokens). */
    val children: List<RedNode>
    /** The range in the tree occupied by this node. */
    val range: Range

    /** Computes the text of this node, including leading and trailing layout. */
    fun text(): String = innerNode.text()
}
