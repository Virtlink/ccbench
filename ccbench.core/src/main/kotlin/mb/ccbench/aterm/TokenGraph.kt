package mb.ccbench.aterm

import mb.spree.green.TextElementKind
import mb.spree.green.Token
import org.spoofax.interpreter.terms.IStrategoString
import org.spoofax.interpreter.terms.IStrategoTerm
import org.spoofax.jsglr.client.imploder.IToken
import org.spoofax.jsglr.client.imploder.ImploderAttachment
import org.spoofax.jsglr2.tokens.treeshaped.TreeToken

/**
 * A graph of [IToken] objects.
 *
 * Multiple tokens may cover the same text due to ambiguities. This class represents a graph of the tokens
 * such that for a given token the left and right tokens are deterministically known.
 */
class TokenGraph {

    class TokenNodeGroup(
        val token: Token,
    ) {
        private val _prev = mutableListOf<TokenNodeGroup>()
        private val _next = mutableListOf<TokenNodeGroup>()
        val previous: List<TokenNodeGroup> get() = _prev
        val next: List<TokenNodeGroup> get() = _next

        val isFirst: Boolean get() = previous.isEmpty()
        val isLast: Boolean get() = next.isEmpty()

        var first: TokenNodeGroup = this
            private set
        var last: TokenNodeGroup = this
            private set

        /**
         * Inserts this node group on the edge between the given node groups.
         */
        fun insertBetween(a: TokenNodeGroup, b: TokenNodeGroup) {

        }
    }


    /**
     * A token node in the graph.
     */
    class TokenNode internal constructor(
        /** The token in the graph. */
        val token: Token,
    ) {
        /** Whether this node is the Beginning-of-File token. */
        val isBof: Boolean get() = token.kind == TextElementKind.BOF
        /** Whether this node is the End-of-File token. */
        val isEof: Boolean get() = token.kind == TextElementKind.EOF

        private val _previousNodes = mutableListOf<TokenNode>()
        private val _nextNodes = mutableListOf<TokenNode>()

        /** The previous node; or `null` when this is the BOF. */
        var previous: TokenNode? = null
            private set
        /** The next node; or `null` when this is the EOF. */
        var next: TokenNode? = null
            private set

        /**
         * Inserts the given token before this node but after the previous node, if any.
         *
         * @param token the token to insert
         * @return the inserted node
         */
        fun insertPrevious(token: Token): TokenNode {
            require(!isBof) { "Cannot insert a node before BOF." }

            val node = TokenNode(token)
            node.previous = this.previous
            node.next = this
            this.previous?.next = node
            this.previous = node
            return node
        }

        /**
         * Inserts the given token after this node but before the next node, if any.
         *
         * @param token the token to insert
         * @return the inserted node
         */
        fun insertNext(token: Token): TokenNode {
            require(!isEof) { "Cannot insert a node after EOF." }

            val node = TokenNode(token)
            node.previous = this
            node.next = this.next
            this.next?.previous = node
            this.next = node
            return node
        }

        /**
         * Removes this node from the graph.
         *
         * @return the token of the removed node
         */
        fun remove(): Token {
            require(!isBof) { "Cannot remove the BOF." }
            require(!isEof) { "Cannot remove the EOF." }

            this.previous?.next = this.next
            this.next?.previous = this.previous
            this.previous = null
            this.next = null
            return this.token
        }

        /**
         * Finds the node that has the specified token,
         * starting from this node.
         *
         * @param token the token to find
         * @return the next node with the specified token, or `null` if there is no such node
         */
        fun findForward(token: Token): TokenNode? {
            var node: TokenNode? = this
            while (node != null) {
                if (node.token == token) {
                    return node
                }
                node = node.next
            }
            return null
        }

    }

    /** The very first node in the graph, indicating the start of the file. */
    val bof: TokenNode = TokenNode(Token.BOF)
    /** The very last node in the graph, indicating the end of the file. */
    val eof: TokenNode = bof.insertNext(Token.EOF)

    companion object {
        fun fromATerm(aterm: IStrategoTerm): TokenGraph {
            TODO()
        }

        fun fromStringTerm(aterm: IStrategoString): TokenGraph {
            // A string term covers the tokens from its left token to its right token

            val leftToken = ImploderAttachment.getLeftToken(aterm)
            val rightToken = ImploderAttachment.getRightToken(aterm)

            val tokensAfterLeft = leftToken.getTokensAfter().filter { it.endOffset <= rightToken.endOffset }

            //
            TODO()
        }

        /**
         * Gets all tokens after this token.
         */
        private fun IToken.getTokensAfter(): Collection<IToken> {
            return if (this is TreeToken) {
                // Tree tokens trivially know what the next tokens are.
                this.tokensAfter
            } else {
                // For other tokens we need to find the next tokens through their offset.
                val endOffset = this.endOffset
                val tokens = this.tokenizer.allTokens().asSequence()
                // Determine the closest offset of the next non-empty token following the current token.
                val nextOffset = tokens
                    .filter { it.startOffset > endOffset && it.isNotEmpty() }
                    .minOf { it.startOffset }
                // Return all tokens with that offset.
                tokens.filter { it.startOffset == nextOffset && it.isNotEmpty() }.toList()
            }
        }

        /**
         * Whether the token is EOF or not empty.
         */
        private fun IToken.isNotEmpty(): Boolean {
            return this.startOffset <= this.endOffset || this.kind == IToken.Kind.TK_EOF
        }
    }
}