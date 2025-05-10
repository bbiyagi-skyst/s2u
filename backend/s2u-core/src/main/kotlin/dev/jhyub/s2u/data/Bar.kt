package dev.jhyub.s2u.data

class Bar(
    val code: String?,
    val lyrics: String?,
    val notes: List<Note>,
    val repeatMark: RepeatMarkType?,
    val pos: CodePosition
): Generator<Bar> {
    override fun generate(): List<Bar> {
        return listOf(this)
    }

    override fun evaluate(context: Map<String, Literal>) {
        notes.forEach { it.evaluate(context) }
    }
}

enum class RepeatMarkType {
    OPEN, CLOSE
}