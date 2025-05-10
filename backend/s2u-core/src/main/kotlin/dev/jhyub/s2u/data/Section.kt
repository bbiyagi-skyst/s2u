package dev.jhyub.s2u.data

data class Section(
    val name: String?,
    val parameterNames: List<String>,
    val rhythm: Rhythm?,
    val tempo: Pair<Rhythm?, Int?>,
    val key: String?,
    val clef: Clef?,
    val generators: List<Generator<Bar>>,
    val pos: CodePosition
) {
    /*
    var bars: List<Bar> = generators.map { it.generate() }.flatten()

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

        bars = generators.map { it.evaluate(newContext); it.generate() }.flatten()
        bars.forEach { it.evaluate(newContext) }
    }

     */
}

data class CalledSection(
    val annotation: List<NoteProperty>,
    val section: Section,
    val parameters: List<Literal>,
    val pos: CodePosition,
    val bars: List<Bar>? = null
): Evaluatable<CalledSection>, Generator<CalledSection> {
    override fun evaluate(context: Context, annotation: List<NoteProperty>): CalledSection {
        if(parameters.size != section.parameterNames.size) throw RuntimeException("Invalid number of parameters (expected ${section.parameterNames.size}, got ${parameters.size}) at ${pos.first}:${pos.second}")
        println(context)
        val oldAnnotation = annotation
        val annotation = this.annotation.applyContext(context)
        val newContext: MutableMap<Literal.NameLiteral, Literal> = context.toMutableMap()
        for ((i, j) in section.parameterNames zip parameters) {
            newContext[Literal.NameLiteral(i)] = j
            println("Adding $i - $j to context")
        }
        val context = newContext.flatten()
        return copy(bars = section.generators.map { it.generate(context, oldAnnotation + annotation) }.flatten())
    }

    override fun generate(context: Context, annotation: List<NoteProperty>): List<CalledSection> {
        return listOf(evaluate(context, annotation))
    }
}
