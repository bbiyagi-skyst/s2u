package dev.jhyub.s2y.data

import dev.jhyub.s2y.SyntaxException

class Section(
    val name: String?,
    val parameters: List<String>,
    val rhythm: Rhythm?,
    val tempo: Pair<String?, Int?>,
    val key: String?,
    val clef: Clef?,
    val generators: List<Generator<Bar>>,
    val pos: CodePosition
) {
    val bars: List<Bar> = generators.map { it.generate() }.flatten().toMutableList()

    fun evaluate(data: List<Literal>, context: Map<String, Literal>) {
        if(parameters.size != data.size) throw SyntaxException("Invalid number of parameters at ${pos.first}:${pos.second}")
        val newContext = context.toMutableMap()
        for ((i, j) in parameters zip data) {
            if(j is Literal.NameLiteral) {
                newContext[i] = context[j.value] ?: throw RuntimeException("Variable ${j.value} does not exist at ${pos.first}:${pos.second}")
                println("Adding $i - ${context[j.value]} to context")
            } else {
                newContext[i] = j
                println("Adding $i - $j to context")
            }
        }

        bars.forEach { it.evaluate(newContext) }
    }
}

class CalledSection(
    val annotation: MutableList<NoteProperty>,
    val section: Section,
    val parameters: List<Literal>,
    val pos: CodePosition
): Generator<CalledSection> {
    override fun generate(): List<CalledSection> {
        return listOf(this)
    }

    fun evaluate(context: Map<String, Literal>) {
        println("evaluating calledsection")
        section.bars.forEach {
            println("for each bar")
            it.notes.forEach { note ->
                println("Added annotation")
                note.properties.addAll(annotation)
            }
        }
        section.evaluate(parameters, context)
    }
}
