package mb.spree.red

import mb.spree.green.Node

/**
 * Lazily creates red nodes for each of the green nodes.
 *
 * Only when a red node is requested for the first time, the corresponding green node is created.
 */
class LazyRedNodeList(
    private val owner: RedTerm,
    private val greenNodes: List<Node>,
): AbstractList<RedNode>() {

    private val redNodes: Array<RedNode?> = arrayOfNulls(greenNodes.size)

    override val size: Int
        get() = redNodes.size

    override fun get(index: Int): RedNode {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index must be between 0 and $size (exclusive), got: $index")
        // Check that the red term is present, and if so, return it.
        val redNode = redNodes[index]
        if (redNode !== null) return redNode
        // The red term was not present. Let's try again, but locking this object.
        return synchronized(this) {
            // In here we can safely check and create the red term without worrying
            // that another thread might have created it in the meantime.

            // Check again whether the red term is present.
            val redNode2 = redNodes[index]
            if (redNode2 === null) {
                // The red term is still not present. We'll create it and store it in the array.
                val greenNode = greenNodes[index]
                val redNode3 = createRedNode(greenNode, index)
                redNodes[index] = redNode3
                redNode3
            } else {
                // The red term is present. Return it.
                redNode2
            }
        }
    }

    private fun createRedNode(greenNode: Node, index: Int): RedTerm {
        TODO()
    }

}
