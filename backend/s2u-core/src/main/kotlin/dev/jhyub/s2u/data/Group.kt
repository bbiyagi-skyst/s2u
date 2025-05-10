package dev.jhyub.s2u.data

import dev.jhyub.s2u.SyntaxException

data class Group(
    val name: String?,
    val parameterNames: List<String>,
    val generators: List<Generator<CalledSection>>,
    val pos: CodePosition
)
/*
{
    var sections: List<CalledSection> = generators.map { it.generate() }.flatten()

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

        sections = generators.map { it.evaluate(newContext); it.generate() }.flatten()
    }
}
 */

data class CalledGroup(
    val annotation: List<NoteProperty>,
    val group: Group,
    val parameters: List<Literal>,
    val pos: CodePosition,
    val sections: List<CalledSection>? = null
): Evaluatable<CalledGroup>, Generator<CalledSection> {
    override fun generate(context: Context, annotation: List<NoteProperty>): List<CalledSection> {
        return evaluate(context, annotation).sections!!
    }

    override fun evaluate(context: Context, annotation: List<NoteProperty>): CalledGroup {
        if(parameters.size != group.parameterNames.size) throw RuntimeException("Invalid number of parameters (expected ${group.parameterNames.size}, got ${parameters.size}) at ${pos.first}:${pos.second}")
        val newContext: MutableMap<Literal.NameLiteral, Literal> = context.toMutableMap()
        val oldAnnotation = annotation
        val annotation = this.annotation.applyContext(context)
        for ((i, j) in group.parameterNames zip parameters) {
            newContext[Literal.NameLiteral(i)] = j
            println("Adding $i - $j to context")
        }
        val context = newContext.flatten()
        return copy(sections = group.generators.map { it.generate(context, oldAnnotation + annotation) }.flatten())
    }

    fun getUnit(): Int {
        var res = 1
        for (section in sections!!) {
            val ret = section.getUnit()
            if (res < ret) res = ret
        }
        return res
    }

    fun translate(
        unit: Rhythm,
        defaultKey: String,
        now: Pair<MutableList<String>, MutableList<String>>
    ): Pair<MutableList<String>, MutableList<String>> {
        val ret = now
        for (section in sections!!) {
            val ret = section.translate(unit, defaultKey, ret)
        }
        return ret
    }
}