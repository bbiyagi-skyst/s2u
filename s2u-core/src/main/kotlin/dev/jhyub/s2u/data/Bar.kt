package dev.jhyub.s2u.data

class Bar(
    val code: String,
    val notes: List<Note>,
    val repeatMark: RepeatMarkType?,
    val pos: CodePosition
): Generator<Bar> {
    override fun generate(): List<Bar> {
        return listOf(this)
    }

    fun evaluate(context: Map<String, Literal>) {
        notes.forEach { it.evaluate(context) }
    }
}

enum class RepeatMarkType {
    OPEN, CLOSE
}