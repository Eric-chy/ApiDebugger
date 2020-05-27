package io.chengguo.api.debugger.lang.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import io.chengguo.api.debugger.lang.ApiLanguage
import io.chengguo.api.debugger.lang.ApiLexer
import io.chengguo.api.debugger.lang.ApiParser
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory
import org.antlr.intellij.adaptor.lexer.TokenIElementType

class ApiSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        init {
            PSIElementTypeFactory.defineLanguageIElementTypes(
                ApiLanguage.INSTANCE,
                ApiParser.tokenNames,
                ApiParser.ruleNames
            )
        }

        private val emptyKeys = arrayOf<TextAttributesKey>()
        val ID = TextAttributesKey.createTextAttributesKey("API_ID", DefaultLanguageHighlighterColors.STRING)
        val LINE_COMMENT = TextAttributesKey.createTextAttributesKey(
            "API_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT
        )
        val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(
            "API_BLOCK_COMMENT",
            DefaultLanguageHighlighterColors.BLOCK_COMMENT
        )
    }

    override fun getHighlightingLexer(): Lexer {
        return ANTLRLexerAdaptor(ApiLanguage.INSTANCE, ApiLexer(null))
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (tokenType !is TokenIElementType) {
            return emptyKeys
        }
        val attrKey = when (tokenType.antlrTokenType) {
            ApiLexer.Keyword -> ID
            ApiLexer.LINE_COMMENT -> LINE_COMMENT
            ApiLexer.COMMENT -> BLOCK_COMMENT
            else -> return emptyKeys
        }
        return arrayOf(attrKey)
    }
}