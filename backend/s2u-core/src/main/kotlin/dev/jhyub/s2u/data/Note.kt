package dev.jhyub.s2u.data

data class Note(
    val pitches: List<Pitch>,
    val rhythm: Rhythm,
    val properties: List<NoteProperty>,
    val finish: Boolean = false, // `true` if semicolon is used
    val legato: LegatoType? = null,
    val triplet: TripletType? = null,
    val codePosition: CodePosition
): Evaluatable<Note>, Generator<Note> {
    override fun evaluate(context: Context, annotation: List<NoteProperty>): Note {
        println("Properties - $properties")
        println("Annotation - $annotation")
        println("Context - $context")
        return copy(
            properties = properties.applyContext(context) + annotation
        )
    }

    override fun generate(
        context: Context,
        annotation: List<NoteProperty>
    ): List<Note> {
        return listOf(evaluate(context, annotation))
    }

    fun translate(
        unit: Rhythm
    ): String {
        if (triplet != null) assert(false)

        var sum: Int = 0
        var result: String = ""
        for (property in properties) {
            if (property is NoteProperty.Up) sum += when (property.x) {
                is Literal.IntLiteral -> (property.x as Literal.IntLiteral).value
                else -> {
                    assert(false)
                    0
                }
            }
            if (property is NoteProperty.Stacato) result += "."
            if (property is NoteProperty.Fermata) result += "!fermata!"
            if (property is NoteProperty.Accent) result += "!>!"
            if (property is NoteProperty.Trill) result += "!trill!"
            if (property is NoteProperty.Tenuto) result += "!tenuto!"
            if (property is NoteProperty.Ornament) result += "{${property.ornament}}"
            if (property is NoteProperty.Marcato) assert(false)
            if (property is NoteProperty.Dynamic) {
                val str = when (property.x) {
                    is Literal.DynamicLiteral -> (property.x as Literal.DynamicLiteral).value.name
                    else -> {
                        assert(false)
                        ""
                    }
                }
                result += "!${str.lowercase()}!"
            }
        }
        // =============================== //
        if (legato == LegatoType.START) {
            result += "("
        }
        // =============================== //
        result += "["
        for (pitch in pitches) {
            result += (pitch + sum).translate()
        }
        result += "]"
        // =============================== //
        result += (rhythm/unit).toString();
        // =============================== //
        if (legato == LegatoType.END) {
            result += ")"
        }
        // =============================== //
        if (finish) result += " "

        return result
    }

    fun getUnit(): Int {
        return rhythm.denominator
    }
}


enum class LegatoType {
    START, END
}

enum class TripletType {
    START, END
}

sealed class NoteProperty {
    data class Up(var x: Literal) : NoteProperty()
    data class Down(var x: Literal) : NoteProperty()
    class Stacato : NoteProperty()
    class Fermata : NoteProperty()
    class Accent : NoteProperty()
    class Trill : NoteProperty()
    class Tenuto : NoteProperty()
    class Ornament(var ornament: Pitch) : NoteProperty()
    class Marcato : NoteProperty()
    data class Dynamic(var x: Literal) : NoteProperty()
}

enum class DynamicValue {
    FF, MF, F, PP, MP, P
}

//fun main() {
//    println(Note(
//        listOf(Pitch(PitchName.D, 4), Pitch(PitchName.A, 4)),
//        Rhythm(1, 2),
//        listOf(NoteProperty.Stacato()),
//        codePosition = 10 to 13
//    ).translate(Rhythm(1, 4)))
//}
