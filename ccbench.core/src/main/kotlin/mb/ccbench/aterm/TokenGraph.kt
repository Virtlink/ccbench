package mb.ccbench.aterm

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