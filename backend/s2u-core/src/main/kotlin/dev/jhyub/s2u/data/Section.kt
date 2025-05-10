package dev.jhyub.s2u.data

import dev.jhyub.s2u.data.NoteProperty
import dev.jhyub.s2u.data.Section

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

    fun translate(
        unit: Rhythm,
        defaultKey: String,
        now: Pair<MutableList<String>, MutableList<String>>
    ): Pair<MutableList<String>, MutableList<String>> {
        if (section.clef == Clef.LOW) assert(false);
        assert(bars != null && bars.isNotEmpty())

        var strings: MutableList<String> = now.first
        var lyrics: MutableList<String> = now.second

        // =============================== //
        if (section.key is String) {
            strings[strings.lastIndex] = "${strings[strings.lastIndex]}[K: ${section.key}]"
        }
        else {
            strings[strings.lastIndex] = "${strings[strings.lastIndex]}[K: ${defaultKey}]"
        }
        // =============================== //
        if (section.tempo.first is Rhythm && section.tempo.second is Int) {
            strings[strings.lastIndex] = "${strings[strings.lastIndex]}[Q: ${(section.tempo.first as Rhythm).numerator}/${(section.tempo.first as Rhythm).denominator}=${section.tempo.second}]"
        }
        // =============================== //
        if (section.rhythm is Rhythm) {
            strings[strings.lastIndex] = "${strings[strings.lastIndex]}[M: ${section.rhythm.numerator}/${section.rhythm.denominator}]"
        }
        // =============================== //
        for (bar in bars!!) {
            val ret = bar.translate(unit)
            strings[strings.lastIndex] = "${strings[strings.lastIndex]}${ret.first}"
            lyrics[strings.lastIndex] = "${lyrics[strings.lastIndex]}${ret.second}|"

            if (countNodes(strings[strings.lastIndex]) == 4) {
                strings.add("")
                lyrics.add("w:")
            }
        }

        return strings to lyrics
    }

    private fun countNodes(string: String): Int {
        return string.count { it == '|' }
    }

    fun getUnit(): Int {
        var res = 1
        for (bar in bars!!) {
            val ret = bar.getUnit()
            if (res < ret) res = ret
        }
        return res
    }
}

//fun main() {
//    println(CalledSection(
//        annotation=listOf(),
//        section=Section(
//            rhythm= Rhythm(4, 4),
//            tempo= Rhythm(1, 4) to 80,
//            key= "Cm",
//            generators= listOf(),
//            pos= 0 to 0,
//            name=null,
//            parameterNames=listOf(),
//            clef=null
//        ),
//        parameters= listOf(),
//        pos= 0 to 0,
//        bars= listOf(
//            Bar(
//                "Cmaj7 - -",
//                "Do Re Mi",
//                listOf(Note(listOf(Pitch(PitchName.C, 4)), Rhythm(1, 4), listOf(),
//                codePosition = 10 to 13), Note(listOf(Pitch(PitchName.D, 4), Pitch(PitchName.A, 4)), Rhythm(1, 2), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.E, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13)),
//                null, 10 to 13
//            ),
//            Bar(
//                "Cmaj7 - -",
//                "Do Re Mi",
//                listOf(Note(listOf(Pitch(PitchName.C, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.D, 4), Pitch(PitchName.A, 4)), Rhythm(1, 2), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.E, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13)),
//                null, 10 to 13
//            ),
//            Bar(
//                "Cmaj7 - -",
//                "Do Re Mi",
//                listOf(Note(listOf(Pitch(PitchName.C, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.D, 4), Pitch(PitchName.A, 4)), Rhythm(1, 2), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.E, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13)),
//                null, 10 to 13
//            ),
//            Bar(
//                "Cmaj7 - -",
//                "Do Re Mi",
//                listOf(Note(listOf(Pitch(PitchName.C, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.D, 4), Pitch(PitchName.A, 4)), Rhythm(1, 2), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.E, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13)),
//                null, 10 to 13
//            ),
//            Bar(
//                "Cmaj7 - -",
//                "Do Re Mi",
//                listOf(Note(listOf(Pitch(PitchName.C, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.D, 4), Pitch(PitchName.A, 4)), Rhythm(1, 2), listOf(),
//                    codePosition = 10 to 13), Note(listOf(Pitch(PitchName.E, 4)), Rhythm(1, 4), listOf(),
//                    codePosition = 10 to 13)),
//                null, 10 to 13
//            )
//        )
//    ).translate(Rhythm(1, 8), "C", mutableListOf("") to mutableListOf("w:")))
//}