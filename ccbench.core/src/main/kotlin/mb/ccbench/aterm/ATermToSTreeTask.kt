package mb.ccbench.aterm

import mb.ccbench.BuildBenchmarkTask
import mb.pie.api.ExecContext
import mb.pie.api.Supplier
import mb.pie.api.TaskDef
import java.io.Serializable
import mb.common.result.Result
import mb.spree.green.*
import org.spoofax.interpreter.terms.*
import org.spoofax.jsglr.client.imploder.IToken
import org.spoofax.jsglr.client.imploder.ImploderAttachment

class ATermToSTreeTask(): TaskDef<ATermToSTreeTask.Input, Result<Node, *>> {
    data class Input(
        val atermSupplier: Supplier<Result<IStrategoTerm, *>>
    ): Serializable

    override fun getId(): String = BuildBenchmarkTask::class.java.name

    override fun exec(ctx: ExecContext, input: Input): Result<Node, *> {
        return input.atermSupplier.get(ctx).map { it ->
            if (it == null) throw NullPointerException("Supplied term was null.")
            TODO()
        }
    }

    private fun convertStringTerm(aterm: IStrategoString): StringTerm {
        val leftToken = ImploderAttachment.getLeftToken(aterm)
        val rightToken = ImploderAttachment.getRightToken(aterm)

        val annotations = aterm.annotations.subterms.map { atermToSTerm(it) }
        return StringTerm(aterm.stringValue(), annotations)
    }

    /**
     * Converts a left ATerm token, a list of nodes (both terms and STree tokens), and a right ATerm token
     * to a list of nodes.
     *
     * @param leftToken the left token
     * @param middleNodes the nodes in between the left and right tokens
     * @param rightToken the right token
     */
    private fun convertToNodes(leftToken: IToken, middleNodes: List<Node>, rightToken: IToken): List<Node> {
        TODO()
    }


    private fun atermToSTerm(aterm: IStrategoTerm): Term { // Triple<Term, IToken, IToken> /* term, left token, right token */ {
        // A text element that is layout belongs to a token.
        // A text element that is not layout, is a token.
        // A token belongs to one (or more, in case of ambiguities) terms.
        // Multiple tokens may cover the same range.

        val leftToken = ImploderAttachment.getLeftToken(aterm)
        val rightToken = ImploderAttachment.getRightToken(aterm)


        val subterms = aterm.subterms.map { atermToSTerm(it) }
        val annotations = aterm.annotations.subterms.map { atermToSTerm(it) }
        val term = when (aterm) {
            is IStrategoPlaceholder -> throw IllegalStateException("Placeholders are not supported: $aterm")
            is IStrategoAppl -> if (aterm.name == "Amb") {
                check(annotations.isEmpty()) { "Annotations on Amb() terms are not supported." }
                AmbTerm(subterms)
            } else {
                ApplTerm(aterm.name, subterms, annotations)
            }
            is IStrategoInt -> IntTerm(aterm.intValue())    // TODO: Get token
            is IStrategoString -> StringTerm(aterm.stringValue())
            is IStrategoList -> {
                check(annotations.isEmpty()) { "Annotations on list terms are not supported." }
                ListTerm(subterms)
            }
            is IStrategoTuple -> ApplTerm("", subterms, annotations)
            is IStrategoReal -> TODO("Real terms are not yet supported.")
            else -> throw IllegalStateException("Unsupported type of ATerm: $aterm")
        }

        return term

//        val tokenMap = mutableMapOf<IToken, Token>()
        // First we gather all the tokens and clean them up. They are the leaves of the tree.
        // We use a map to track the correspondence of the ATerm tokens to the STree tokens.
        // Because of ambiguities in the parse, different tokens may be returned that cover the same range of document.

        // First we gather all the text elements. These include actual syntax, as well as layout.
        // If a token contains a newline, it will be split into multiple tokens.
        // We maintain a map of IToken to a list of text elements. The tokens in each group are ordered by their start offset.
        val textElementMap = textElementsFromATermTokens(ImploderAttachment.getTokenizer(aterm).allTokens())
            .sortedBy { it.startOffset }
            .groupBy { it.token }


        // We also gather

        // We order the text elements by start offset. Multiple text elements may have the same start offset,
        // or overlap in other ways, due to ambiguities in the parsing.

    }

    private fun textElementsFromATermTokens(tokens: Iterable<IToken>): Sequence<TmpTextElement> = sequence {
        for (token in tokens) {
            val content = token.toString()
            val kind = getTextElementKindFromATermToken(token)
            val isLayout = isLayoutFromATermToken(token)

            var lineStartIndex = 0       // Start index of the current line
            var lineEndIndex = -1        // End index of the last line
            lineEndIndex = content.indexOf('\n', lineEndIndex)
            while (lineEndIndex >= 0) {
                val lineContent = content.substring(lineStartIndex, lineEndIndex + 1)   // We include the newline character
                yield(TmpTextElement(
                    lineContent,
                    kind,
                    isLayout,
                    token.startOffset + lineStartIndex,
                    token.startOffset + lineEndIndex + 1,
                    token,
                ))
                lineStartIndex = lineEndIndex + 1
                lineEndIndex = content.indexOf('\n', lineEndIndex)
            }
            val lineContent = content.substring(lineStartIndex)
            yield(TmpTextElement(
                lineContent,
                kind,
                isLayout,
                token.startOffset + lineStartIndex,
                token.startOffset + lineEndIndex + 1,
                token,
            ))
            lineStartIndex = lineEndIndex + 1
        }
    }

    /**
     * Gets the text elements that make up the specified ATerm token.
     *
     * If a token crosses the newline boundary, it is split into multiple tokens.
     *
     * @param token the input token
     * @return the list of resulting text elements
     */
    private fun textElementsFromATermToken(token: IToken): Sequence<TmpTextElement> = sequence {
        val content = token.toString()
        val kind = getTextElementKindFromATermToken(token)
        val isLayout = isLayoutFromATermToken(token)

        var lineStart = 0       // Start index of the current line
        var lineEnd = 0         // End index of the current line
        lineEnd = content.indexOf('\n', lineEnd)
        while (lineEnd >= 0) {
            val lineContent = content.substring(lineStart, lineEnd + 1)   // We include the newline character
            yield(TmpTextElement(lineContent, kind, isLayout, lineStart, lineEnd, TODO()))
            lineStart = lineEnd + 1
            lineEnd = content.indexOf('\n', lineEnd)
        }
    }

    private fun getTextElementKindFromATermToken(token: IToken): TextElementKind {
        return when (token.kind) {
            IToken.Kind.TK_IDENTIFIER -> TextElementKind("entity")
            IToken.Kind.TK_NUMBER -> TextElementKind("contant.numeric")
            IToken.Kind.TK_STRING -> TextElementKind("string")
            IToken.Kind.TK_KEYWORD -> TextElementKind("keyword")
            IToken.Kind.TK_OPERATOR -> TextElementKind("keyword.operator")
            IToken.Kind.TK_VAR -> TextElementKind("variable")
            IToken.Kind.TK_LAYOUT -> TextElementKind("comment")
            IToken.Kind.TK_EOF -> TextElementKind("layout")
            IToken.Kind.TK_ERROR -> TextElementKind("invalid")
            IToken.Kind.TK_ERROR_KEYWORD -> TextElementKind("invalid.keyword")
            IToken.Kind.TK_ERROR_LAYOUT -> TextElementKind("invalid.comment")
            IToken.Kind.TK_ERROR_EOF_UNEXPECTED -> TextElementKind("invalid.layout")
            IToken.Kind.TK_ESCAPE_OPERATOR -> TextElementKind("keyword.operator")
            IToken.Kind.TK_RESERVED -> TextElementKind("keyword")
            else -> TextElementKind("")
        }
    }

    private fun isLayoutFromATermToken(token: IToken): Boolean {
        return when (token.kind) {
            IToken.Kind.TK_LAYOUT -> true
            IToken.Kind.TK_EOF -> true
            IToken.Kind.TK_ERROR_LAYOUT -> true
            IToken.Kind.TK_ERROR_EOF_UNEXPECTED -> true
            else -> false
        }
    }

    /**
     * A text element used in the conversion from ATerms to STrees.
     */
    private data class TmpTextElement(
        override val content: String,
        override val kind: TextElementKind,
        override val isLayout: Boolean,
        val startOffset: Int,
        val endOffset: Int,
        /** The token from which this text element was created. One token may give rise to multiple text elements. */
        val token: IToken,
    ): TextElement {
        val isEmpty: Boolean get() = content.isEmpty()
        /** Whether the content ends with a newline. */
        val isEol: Boolean get() = content.endsWith('\n')
        override val allTextElements: Sequence<TextElement>
            get() = sequenceOf(this)
    }
}