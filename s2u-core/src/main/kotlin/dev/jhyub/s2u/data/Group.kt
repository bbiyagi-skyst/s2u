package dev.jhyub.s2u.data

import dev.jhyub.s2u.SyntaxException

class Group(
    val name: String?,
    val parameters: List<String>,
    val generators: List<Generator<CalledSection>>,
    val pos: CodePosition
) {
    val sections: List<CalledSection> = generators.map { it.generate() }.flatten().toMutableList()

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

        sections.forEach { it.evaluate(newContext) }
    }
}

class CalledGroup(
    val annotation: MutableList<NoteProperty>,
    val group: Group,
    val parameters: List<Literal>,
    val pos: CodePosition
): Generator<CalledSection> {
    override fun generate(): List<CalledSection> {
        return group.sections
    }

    fun evaluate(context: Map<String, Literal>) {
        println("evaluating calledgroup")
        group.sections.forEach {
            it.annotation.addAll(annotation)
        }
        group.evaluate(parameters, context)
    }
}