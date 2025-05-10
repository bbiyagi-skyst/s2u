package dev.jhyub.s2y.data

class Note(
    val pitches: List<Pitch>,
    val rhythm: Rhythm,
    val properties: MutableList<NoteProperty>,
    val codePosition: CodePosition
) {
    fun evaluate(context: Map<String, Literal>) {
        for (i in properties) {
            if (i is NoteProperty.Up && i.x is Literal.NameLiteral) {
                val key = (i.x as Literal.NameLiteral).value
                i.x = context[key] ?: throw RuntimeException("Variable $key does not exist")
            }
            if (i is NoteProperty.Down && i.x is Literal.NameLiteral) {
                val key = (i.x as Literal.NameLiteral).value
                i.x = context[key] ?: throw RuntimeException("Variable $key does not exist")
            }
            if (i is NoteProperty.Dynamic && i.x is Literal.NameLiteral) {
                val key = (i.x as Literal.NameLiteral).value
                i.x = context[key] ?: throw RuntimeException("Variable $key does not exist")
            }
        }
    }
}

sealed class NoteProperty {
    class Up(var x: Literal) : NoteProperty()
    class Down(var x: Literal) : NoteProperty()
    class Stacato : NoteProperty()
    class Fermata : NoteProperty()
    class Accent : NoteProperty()
    class Trill : NoteProperty()
    class Tenuto : NoteProperty()
    class Ornament : NoteProperty()
    class Marcato : NoteProperty()
    class Dynamic(var x: Literal) : NoteProperty()
}

enum class DynamicValue {
    FF, MF, F, PP, MP, P
}
