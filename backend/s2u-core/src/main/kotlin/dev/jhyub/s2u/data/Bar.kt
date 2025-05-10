package dev.jhyub.s2u.data

data class Bar(
    val code: String?,
    val lyrics: String?,
    val notes: List<Generator<Note>>,
//    val notes: List<Note>,
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

    fun translate(
        unit: Rhythm
    ) : Pair<String, String> {
        var res: String = ""
        // =============================== //
        if (repeatMark == RepeatMarkType.OPEN) {
            if (res[res.lastIndex] == '|') {
                res = "$res:"
            }
            else {
                res = "$res|:"
            }
        }
        // =============================== //
        if (code !is String) {
            var code: String = "-"
            for (i in 2..notes.size) code += " -"
        }
        val codeList = code!!.split(" ")
        for ((i, _note) in notes.withIndex()) {
            val note = _note as Note
            if (codeList[i] != "-") res = "${res}\"${codeList[i]}\""
            res = "${res}${note.translate(unit)}"
        }
        // =============================== //
        if (repeatMark == RepeatMarkType.OPEN) {
            res = "$res:|"
        }
        else {
            res = "$res|"
        }

        return res to if (lyrics is String) lyrics else ""
    }

    fun getUnit(): Int {
        var res = 1
        for (_note in notes) {
            val note = _note as Note
            val ret = note.getUnit()
            if (res < ret) res = ret
        }
        return res
    }
}

enum class RepeatMarkType {
    OPEN, CLOSE
}

//fun main() {
//    println(Bar(
//        "Cmaj7 - -",
//        "Do Re Mi",
//        listOf(Note(listOf(Pitch(PitchName.C, 4)), Rhythm(1, 4), listOf(),
//        codePosition = 10 to 13), Note(listOf(Pitch(PitchName.D, 4), Pitch(PitchName.A, 4)), Rhythm(1, 2), listOf(),
//            codePosition = 10 to 13), Note(listOf(Pitch(PitchName.E, 4)), Rhythm(1, 4), listOf(),
//            codePosition = 10 to 13)),
//        null, 10 to 13
//    ).translate(Rhythm(1, 4)))
//}