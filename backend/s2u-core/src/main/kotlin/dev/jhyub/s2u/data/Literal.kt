package dev.jhyub.s2u.data

sealed class Literal {
    data class IntLiteral(val value: Int): Literal()

    data class StringLiteral(val value: String): Literal()

    data class PitchLiteral(val value: Pitch): Literal()

    data class RhythmLiteral(val value: Rhythm): Literal()

    data class CodeLiteral(val value: String): Literal()

    data class KeyLiteral(val value: String): Literal()

    data class DynamicLiteral(val value: DynamicValue): Literal()

    data class NameLiteral(val value: String): Literal()
}