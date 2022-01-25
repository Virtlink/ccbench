package mb.spree.red

import mb.spree.green.Term

/**
 * Lazily creates red entries for each of the green entries.
 *
 * Only when a red entry is requested for the first time, the corresponding green entry is created.
 */
class LazyRedTermMap(
    private val owner: RedTerm,
    private val greenMap: Map<Term, Term>,
): AbstractMap<RedTerm, RedTerm>() {

    private val redTerms: Array<RedTerm?> = arrayOfNulls(greenMap.size)

    override val entries: Set<Map.Entry<RedTerm, RedTerm>>
        get() = TODO("Not yet implemented")

}
