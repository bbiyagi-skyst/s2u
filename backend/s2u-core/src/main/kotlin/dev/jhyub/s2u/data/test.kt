package dev.jhyub.s2u.data

fun main() {
    val n = Note(
        listOf(Pitch(PitchName.C, 4)),
        Rhythm(1, 4),
        properties = listOf(NoteProperty.Up(Literal.NameLiteral("k"))),
        codePosition = 0 to 0
    )

    val loop = Loop(
        end = 4,
        indexName = "k",
        generators = listOf(n),
    )

    val b = Bar(
        "Cm",
        null,
        listOf(loop),
        null,
        0 to 0
    )

    val loop2 = Loop(
        end = 2,
        indexName = "j",
        generators = listOf(b)
    )

    val s = Section(
        name = "s",
        parameterNames = listOf("x"),
        rhythm = Rhythm(4, 4),
        tempo = Rhythm(1, 4) to 80,
        key = "Cm",
        clef = Clef.HIGH,
        generators = listOf(loop2),
        pos = 0 to 0
    )

    val cs = CalledSection(
        annotation = listOf(NoteProperty.Down(Literal.IntLiteral(2))),
        section = s,
        parameters = listOf(Literal.NameLiteral("i")),
        0 to 0
    )

    val loop3 = Loop(
        end = 3,
        generators = listOf(cs)
    )

    val g = Group(
        name = null,
        parameterNames = listOf(),
        generators = listOf(loop3),
        pos = 0 to 0
    )

    val cg = CalledGroup(
        annotation = listOf(),
        group = g,
        parameters = listOf(),
        pos = 0 to 0
    )


    print(cg.evaluate(mapOf(), listOf()))


}