package dev.jhyub.s2u.data

data class Bar(
    val code: String?,
    val lyrics: String?,
    val notes: List<Generator<Note>>,
    val repeatMark: RepeatMarkType?,
    val pos: CodePosition
): Generator<Bar>, Evaluatable<Bar> {
    override fun evaluate(context: Context, annotation: List<NoteProperty>): Bar {
        return copy(
            notes = notes.map { it.generate(context, annotation) }.flatten()
        )
    }

    override fun generate(context: Context, annotation: List<NoteProperty>): List<Bar> {
        return listOf(evaluate(context, annotation))
    }
}

enum class RepeatMarkType {
    OPEN, CLOSE
}