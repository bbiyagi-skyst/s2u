package dev.jhyub.s2u.frontend

import dev.jhyub.s2u.SyntaxException
import dev.jhyub.s2u.data.CodePosition
import dev.jhyub.s2u.data.DynamicValue
import dev.jhyub.s2u.data.Literal
import dev.jhyub.s2u.data.NoteProperty
import dev.jhyub.s2u.data.RepeatMarkType
import dev.jhyub.s2u.data.Rhythm


data class File(val blocks: List<Block>) {
    companion object {
        fun fromTokens(tokens: List<Pair<Token, CodePosition>>): File {
            val blocks = mutableListOf<Block>()
            var idx = 0
            var start = 0
            var cnt = 0
            while (tokens[idx].first is Token.NEWLINE) {
                idx++
                start = idx
            }
            while (idx < tokens.size) {
                if (tokens[idx].first is Token.CURLY_BRACKET_START) {
                    cnt++
                }
                if (tokens[idx].first is Token.CURLY_BRACKET_END) {
                    if (--cnt == 0) {
                        blocks.add(Block.fromTokens(tokens.subList(start, idx + 1)))
                        start = idx + 1
                    }
                }
                idx++
            }
            return File(blocks)
        }

  }
}

fun literalFromTokens(tokens: List<Pair<Token, CodePosition>>): Literal {
    if (tokens.size == 1) {
        val token = tokens[0]
        if (token.first is Token.NUMBER_LITERAL) {
            return Literal.IntLiteral((token.first as Token.NUMBER_LITERAL).content)
        } else if (token.first is Token.STRING_LITERAL) {
            return Literal.StringLiteral((token.first as Token.STRING_LITERAL).content)
        } else if (token.first is Token.IDENTIFIER) {
            val idToDynVal = mapOf(
                "ff" to DynamicValue.FF,
                "f" to DynamicValue.F,
                "mf" to DynamicValue.MF,
                "pp" to DynamicValue.PP,
                "p" to DynamicValue.P,
                "mp" to DynamicValue.MP
            )
            if((token.first as Token.IDENTIFIER).value in idToDynVal) {
                return Literal.DynamicLiteral(idToDynVal[(token.first as Token.IDENTIFIER).value]!!)
            }
            return Literal.NameLiteral((token.first as Token.IDENTIFIER).value)
        }
    } else if (tokens.size == 3) {
        if (tokens[1].first is Token.SLASH) {
            return Literal.RhythmLiteral(Rhythm(((tokens[0].first as Token.NUMBER_LITERAL).content), (tokens[2].first as Token.NUMBER_LITERAL).content))
        }
    } else {

    }
    throw SyntaxException("Unknown literal at ${tokens[0].second.first}:${tokens[0].second.second}")

}

fun notePropertyFromTokens(tokens: List<Pair<Token, CodePosition>>): NoteProperty {
    println("note property parser got $tokens")
    return if (tokens.size == 1) {
        when((tokens[0].first as Token.IDENTIFIER).value) {
            "s" -> NoteProperty.Stacato()
            "f" -> NoteProperty.Fermata()
            "a" -> NoteProperty.Accent()
            "tr" -> NoteProperty.Trill()
            "t" -> NoteProperty.Tenuto()
            "m" -> NoteProperty.Marcato()
            else -> throw SyntaxException("Unknown note property at ${tokens[0].second.first}:${tokens[0].second.second}")
        }
    } else {
        when((tokens[0].first as Token.IDENTIFIER).value) {
            "u" -> NoteProperty.Up(literalFromTokens(tokens.subList(2, tokens.size - 1)))
            "dy" -> NoteProperty.Dynamic(literalFromTokens(tokens.subList(2, tokens.size - 1)))
            else -> throw SyntaxException("Unknown note property at ${tokens[0].second.first}:${tokens[0].second.second}")
        }
    }

}

data class Attribute(val name: String, val value: List<Literal>, val noteProperty: List<NoteProperty>, val codePosition: CodePosition) {
    companion object {
        fun fromTokens(tokens: List<Pair<Token, CodePosition>>): Attribute {
            if(tokens[1].first !is Token.COLON) throw SyntaxException("Colon expected at ${tokens[1].second.first}:${tokens[1].second.second}")
            val name = (tokens[0].first as Token.IDENTIFIER).value
            val value = mutableListOf<Literal>()
            val noteProperty = mutableListOf<NoteProperty>()
            println("attribute parser got $tokens")

            var idx = 2
            var start = 2
            while (idx < tokens.size) {
                if (tokens[idx].first is Token.COMMA || tokens[idx].first is Token.NEWLINE || idx == tokens.size - 1) {
                    if(idx == tokens.size - 1) idx++
                    val sublist = tokens.subList(start, idx)
                    var isNoteProperty = false
                    for ((i, _) in sublist) {
                        if(i is Token.IDENTIFIER)
                            if(i.value in listOf("s", "u", "o", "f", "a", "d", "tr", "t", "m", "dy")) {
                                isNoteProperty = true
                                break
                            }
                    }
                    if(isNoteProperty) {
                        val np = notePropertyFromTokens(sublist)
                        noteProperty.add(np)

                    } else {
                        val literal = literalFromTokens(sublist)
                        value.add(literal)
                    }
                    start = ++idx
                    continue
                }
                idx++
            }

            return Attribute(name, value, noteProperty, tokens[0].second)
        }
    }
}
data class Block(val type: String, val name: String?, val attributes: List<Attribute>, val content: List<Called>, val codePosition: CodePosition) {
    companion object {
        fun fromTokens(tokens: List<Pair<Token, CodePosition>>): Block {
            var idx = 0
            var start = 0
            while(tokens[idx].first is Token.NEWLINE) {
                idx++
                start = idx
            }

            val codePosition = tokens[idx].second
            val type: String
            val name: String?

            if (tokens[idx].first is Token.SHEET)  {
                type = "sheet"
            } else if (tokens[idx].first is Token.SECTION) {
                type = "section"
            } else {
                throw SyntaxException("Wrong block leader at ${tokens[idx].second.first}:${tokens[idx].second.second}")
            }

            if (tokens[idx+1].first is Token.CURLY_BRACKET_START) { // Anon
                name = null
                idx = idx + 2
            } else if (tokens[idx+1].first is Token.IDENTIFIER) {
                name = (tokens[idx+1].first as Token.IDENTIFIER).value
                idx = idx + 3
            } else {
                throw SyntaxException("Identifier expected at ${tokens[idx+1].second.first}:${tokens[idx+1].second.second}")
            }

            val body = tokens.subList(idx, tokens.size-1)
            idx = 0
            start = 0

            while(body[idx].first is Token.NEWLINE) {
                start = ++idx
            }

            var trySeeingColon = true
            var haveSeenColon = false
            val attributes = mutableListOf<Attribute>()
            val content = mutableListOf<Called>()
            while(idx < body.size) {

                println("current start: $start idx: $idx")

                if (body[idx].first is Token.COLON) {
                    println("saw colon at $idx")
                    if (trySeeingColon) haveSeenColon = true
                    idx++
                    continue
                }
                if (body[idx].first is Token.NEWLINE) {
                    println("saw nl at $idx")
                    if (haveSeenColon) {
                        attributes.add(Attribute.fromTokens(body.subList(start, idx)))
                        start = ++idx
                        haveSeenColon = false
                    } else {
                        trySeeingColon = false
                        if(body[start].first is Token.IDENTIFIER) {
                            if((body[start].first as Token.IDENTIFIER).value == "loop") {
                                val loop = Loop.fromTokens(body.subList(start, idx))
                                content.add(loop)
                                start = ++idx
                                continue
                            }
                        }
                        val singleInvocation = SingleInvocation.fromTokens(body.subList(start, idx))
                        content.add(singleInvocation)
                        start = ++idx
                        continue
                    }
                }
                idx++
            }

            return Block(type, name, attributes, content, codePosition = codePosition)
        }
    }
}
sealed class Called
data class Loop(val end: Int, val start: Int?, val step: Int?, val indexName: String?, val content: List<Called>, val codePosition: CodePosition): Called() {
    companion object {
        fun fromTokens(tokens: List<Pair<Token, CodePosition>>): Loop {
            TODO()
        }
    }
}
data class SingleInvocation(val annotations: List<NoteProperty>, val name: String, val arguments: List<Literal>, val codePosition: CodePosition): Called() {
    companion object {
        fun fromTokens(tokens: List<Pair<Token, CodePosition>>): SingleInvocation {
            println("SI - $tokens")
            val annotations = mutableListOf<NoteProperty>()
            var name = ""
            val arguments = mutableListOf<Literal>()
            var idx = 0
            var start = 0
            while(idx < tokens.size) {
                if(tokens[idx].first is Token.AT) {
                    val annotation: NoteProperty
                    if(tokens[idx+2].first is Token.SMALL_BRACKET_START) { // AT with parameters
                        annotation = notePropertyFromTokens(tokens.subList(idx+1, idx+5))
                        idx += 5
                        start = idx
                    } else {
                        annotation = notePropertyFromTokens(tokens.subList(idx+1, idx+2))
                        idx += 2
                        start = idx
                    }
                    annotations.add(annotation)
                }
                if(tokens[idx].first is Token.SMALL_BRACKET_START) {
                    name = (tokens[idx-1].first as Token.IDENTIFIER).value
                    start = idx+1
                }
                if(tokens[idx].first is Token.COMMA || tokens[idx].first is Token.SMALL_BRACKET_END) {
                    if(start != idx) {
                        val literal = literalFromTokens(tokens.subList(start, idx))
                        arguments.add(literal)
                    }
                    start = idx+1
                }
                idx++
            }
            return SingleInvocation(annotations, name, arguments, codePosition = tokens[0].second)
        }
    }
}

data class Bar(val repeatMarkType: RepeatMarkType?, val attributes: List<Attribute>) {
    companion object {
        fun fromTokens(tokens: List<Pair<Token, CodePosition>>): Bar {
            var idx = 0
            var end = 0
            val repeatMarkType: RepeatMarkType?
            if (tokens[1].first is Token.COLON) {
                repeatMarkType = RepeatMarkType.OPEN
                idx = 2
                end = tokens.size - 1
            } else if (tokens[tokens.size - 2].first is Token.COLON) {
                repeatMarkType = RepeatMarkType.CLOSE
                idx = 1
                end = tokens.size - 2
            } else {
                repeatMarkType = null
                idx = 1
                end = tokens.size - 1
            }

            val body = tokens.subList(idx, end)
            idx = 0
            var start = 0

            while (body[idx].first is Token.NEWLINE) {
                start = ++idx
            }

            var trySeeingColon = true
            var haveSeenColon = false
            val attributes = mutableListOf<Attribute>()
            val content = mutableListOf<Called>()
            while (idx < body.size) {

                println("current start: $start idx: $idx")

                if (body[idx].first is Token.COLON) {
                    println("saw colon at $idx")
                    if (trySeeingColon) haveSeenColon = true
                    idx++
                    continue
                }
                if (body[idx].first is Token.NEWLINE) {
                    println("saw nl at $idx")
                    if (haveSeenColon) {
                        attributes.add(Attribute.fromTokens(body.subList(start, idx)))
                        start = ++idx
                        haveSeenColon = false
                    } else {
                        trySeeingColon = false
                        if (body[start].first is Token.IDENTIFIER) {
                            if ((body[start].first as Token.IDENTIFIER).value == "loop") {
                                val loop = Loop.fromTokens(body.subList(start, idx))
                                content.add(loop)
                                start = ++idx
                                continue
                            }
                        }
                        val singleInvocation = SingleInvocation.fromTokens(body.subList(start, idx))
                        content.add(singleInvocation)
                        start = ++idx
                        continue
                    }
                }
                idx++
            }
            return Bar(repeatMarkType, attributes)
        }
    }

}