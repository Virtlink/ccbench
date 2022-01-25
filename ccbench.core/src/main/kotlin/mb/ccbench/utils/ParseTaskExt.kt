package mb.ccbench.utils

import mb.common.message.KeyedMessages
import mb.jsglr.common.JsglrParseException
import mb.jsglr.common.JsglrParseExceptions
import mb.jsglr.pie.JsglrParseTaskDef
import mb.jsglr.pie.JsglrParseTaskInput
import mb.pie.api.ExecContext
import mb.resource.ResourceKey
import org.spoofax.interpreter.terms.IStrategoTerm
import kotlin.streams.asSequence

/**
 * Parses the resource with the specified key into an ATerm.
 *
 * @param ctx the execution context
 * @param resourceKey the resource key
 * @return the ATerm
 */
fun JsglrParseTaskDef.runParse(ctx: ExecContext, resourceKey: ResourceKey, originalResourceKey: ResourceKey): IStrategoTerm {
    val jsglrResult = try {
        ctx.require(
            this, JsglrParseTaskInput.builder()
                .withFile(resourceKey)
                .build()
        ).unwrap()
    } catch (ex: JsglrParseException) {
        val messages: KeyedMessages? = ex.optionalMessages.orElse(null)
        throw RuntimeException(parseErrorToString(resourceKey, originalResourceKey, ex.message, messages), ex)
    }
    check(!jsglrResult.ambiguous) { parseErrorToString(resourceKey, originalResourceKey, "Parse result is ambiguous.", null) }
    check(!jsglrResult.messages.containsErrorOrHigher()) { parseErrorToString(resourceKey, originalResourceKey, null, jsglrResult.messages) }
    return jsglrResult.ast
}

private fun parseErrorToString(resourceKey: ResourceKey, originalResourceKey: ResourceKey, text: String?, messages: KeyedMessages?): String {
    val actualText = text ?: "Parse result has errors."
    val resourceName = if (resourceKey == originalResourceKey) "$resourceKey" else "$originalResourceKey ($resourceKey)"
    if (messages == null) return "$resourceName: $actualText"
    return "$resourceName: $actualText\n" + messages.stream().asSequence().joinToString { "- ${it.region}: ${it.text}\n" }
}
