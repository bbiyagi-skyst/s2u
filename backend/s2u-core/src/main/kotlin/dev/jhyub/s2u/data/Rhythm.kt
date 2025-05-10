package dev.jhyub.s2u.data

data class Rhythm(val numerator: Int, val denominator: Int) {
    operator fun div(other: Rhythm): Int {
        val new_numerator: Int = numerator * other.denominator
        val new_denominator: Int = denominator * other.numerator
        assert(new_numerator % new_denominator == 0)
        return new_numerator / new_denominator
    }
}