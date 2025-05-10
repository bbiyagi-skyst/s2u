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
    class Ornament : NoteProperty()
    class Marcato : NoteProperty()
    data class Dynamic(var x: Literal) : NoteProperty()
}

enum class DynamicValue {
    FF, MF, F, PP, MP, P
}
