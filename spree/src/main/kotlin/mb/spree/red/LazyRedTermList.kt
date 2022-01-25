package mb.spree.red

import mb.spree.green.Term

/**
 * Lazily creates red terms for each of the green terms.
 *
 * Only when a red term is requested for the first time, the corresponding green term is created.
 */
class LazyRedTermList(
    private val owner: RedTerm,
    private val greenTerms: List<Term>,
): AbstractList<RedTerm>() {

    private val redTerms: Array<RedTerm?> = arrayOfNulls(greenTerms.size)

    override val size: Int
        get() = redTerms.size

    override fun get(index: Int): RedTerm {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index must be between 0 and $size (exclusive), got: $index")
        // Check that the red term is present, and if so, return it.
        val redTerm = redTerms[index]
        if (redTerm !== null) return redTerm
        // The red term was not present. Let's try again, but locking this object.
        return synchronized(this) {
            // In here we can safely check and create the red term without worrying
            // that another thread might have created it in the meantime.

            // Check again whether the red term is present.
            val redTerm2 = redTerms[index]
            if (redTerm2 === null) {
                // The red term is still not present. We'll create it and store it in the array.
                val greenTerm = greenTerms[index]
                val redTerm3 = createRedTerm(greenTerm, index)
                redTerms[index] = redTerm3
                redTerm3
            } else {
                // The red term is present. Return it.
                redTerm2
            }
        }
    }

    private fun createRedTerm(greenTerm: Term, index: Int): RedTerm {
        TODO()
    }

}
