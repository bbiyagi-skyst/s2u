package dev.jhyub.s2u.frontend

import dev.jhyub.s2u.data.CodePosition

sealed class Token() {
    class SHEET(): Token() { override fun toString(): String = "SHEET" }
    class SECTION(): Token() { override fun toString(): String = "SECTION" }

    class SMALL_BRACKET_START(): Token() { override fun toString(): String = "(" }
    class SMALL_BRACKET_START_AND_THREE(): Token() { override fun toString(): String = "(3"}
    class SMALL_BRACKET_END(): Token() { override fun toString(): String = ")" }
    class CURLY_BRACKET_START(): Token() { override fun toString(): String = "{" }
    class CURLY_BRACKET_END(): Token() { override fun toString(): String = "}" }
    class BIG_BRACKET_START(): Token() { override fun toString(): String = "[" }
    class BIG_BRACKET_END(): Token() { override fun toString(): String = "]" }

    class SLASH(): Token() { override fun toString(): String = "/" }
    class COLON(): Token() { override fun toString(): String = ":" }
    class SEMICOLON(): Token() { override fun toString(): String = ";" }
    class NEWLINE(): Token() { override fun toString(): String = "NL" }
    class COMMA(): Token() { override fun toString(): String = "," }
    class AT(): Token() { override fun toString(): String = "@" }
    class ARROW(): Token() { override fun toString(): String = "->" }
    class PLUS(): Token() { override fun toString(): String = "+" }

    data class IDENTIFIER(val value: String): Token() { override fun toString(): String = "ID($value)" }

    data class STRING_LITERAL(val content: String): Token() { override fun toString(): String = "STR($content)" }
    data class NUMBER_LITERAL(val content: Int): Token() { override fun toString(): String = "NUM($content)" }
}

fun String.tokenize(): List<Pair<Token, CodePosition>> {
    val ret = mutableListOf<Pair<Token, CodePosition>>()

    val contents = split("\n")
    val finalContents = mutableListOf<Pair<String, CodePosition>>()
    val twoLetterSplitKeyword = listOf("(3", "->")
    val splitKeyword = listOf(
        '(', ')', '{', '}', '[', ']', '/', ':', ';', '\n', ',', '@', '+'
    )
    for ((line, i) in contents.withIndex()) {
        if(i.isEmpty()) continue
        var idx = 0
        var last = 0
        var sawQuote = false
        while (idx < i.length) {
            if (!sawQuote && (i[idx] == ' ' || i[idx] == '\t')) {
                if(last != idx) {
                    finalContents.add(i.substring(last, idx) to CodePosition(line+1, last+1))
                }
                last = ++idx
                continue
            }
            if (i[idx] == '"') sawQuote = !sawQuote
            if (idx < i.length - 1) {
                if("${i[idx]}${i[idx+1]}" in twoLetterSplitKeyword) {
                    if (idx != last) finalContents.add(i.substring(last, idx) to CodePosition(line+1, last+1))
                    finalContents.add(i.substring(idx, idx+2) to CodePosition(line+1, idx+1))
                    idx += 2
                    last = idx
                    continue
                }
            }
            if (i[idx] in splitKeyword) {
                if(idx != last) finalContents.add(i.substring(last, idx) to CodePosition(line+1, last+1))
                finalContents.add(i[idx].toString() to CodePosition(line+1, idx+1))
                last = idx + 1
            }
            idx++
        }
        if(last != i.length) finalContents.add(i.substring(last, i.length) to CodePosition(line+1, last+1))
        finalContents.add("\n" to CodePosition(line+1, i.length))
    }
    val keywords = mapOf(
        "(3" to Token.SMALL_BRACKET_START_AND_THREE(),
        "sheet" to Token.SHEET(),
        "sec" to Token.SECTION(),
        "{" to Token.CURLY_BRACKET_START(),
        "}" to Token.CURLY_BRACKET_END(),
        "(" to Token.SMALL_BRACKET_START(),
        ")" to Token.SMALL_BRACKET_END(),
        "[" to Token.BIG_BRACKET_START(),
        "]" to Token.BIG_BRACKET_END(),
        "/" to Token.SLASH(),
        ":" to Token.COLON(),
        ";" to Token.SEMICOLON(),
        "\n" to Token.NEWLINE(),
        "," to Token.COMMA(),
        "@" to Token.AT(),
        "->" to Token.ARROW(),
        '+' to Token.PLUS()
    )
    finalContents.map {(a, b) -> (if(a != "\n") a.trim() else a) to b}.forEach { (it, pos) ->
        var flag = false
        keywords.forEach { (k, v) ->
            if (it == k) {
                ret.add(v to pos)
                flag = true
                return@forEach
            }
        }
        if (flag) return@forEach

        if (it.startsWith('"') && it.endsWith('"')) {
            ret.add(Token.STRING_LITERAL(it.removeSurrounding("\"")) to pos)
            return@forEach
        }

        if (it.toIntOrNull() != null) {
            ret.add(Token.NUMBER_LITERAL(it.toInt()) to pos)
            return@forEach
        }

        if(it.isEmpty()) return@forEach

        ret.add(Token.IDENTIFIER(it) to pos)
    }

    return ret
}
