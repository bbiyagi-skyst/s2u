package dev.jhyub.s2y.data

class Bar(
    val code: String,
    val notes: List<Note>,
    val pos: CodePosition
): Generator<Bar> {
    override fun generate(): List<Bar> {
        return listOf(this)
    }

    fun evaluate(context: Map<String, Literal>) {
        notes.forEach { it.evaluate(context) }
    }
}