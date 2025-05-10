package dev.jhyub.s2u.data

data class Sheet(
    val title: String,
    val composer: String,
    val group: CalledGroup,
    val rhythm: Rhythm?,
    val tempo: Pair<String?, Int?>,
    val key: String?,
    val clef: Clef?,
    val pos: CodePosition
): Evaluatable<Sheet> {
    override fun evaluate(context: Context, annotation: List<NoteProperty>): Sheet {
        return copy(
            group = group.evaluate(context, annotation)
        )
    }

    fun translate(): String {
        if (clef == Clef.LOW) assert(false)

        var res: String = "X:1\n"
        // =============================== //
        res = "${res}T:${title}\n"
        // =============================== //
        res = "${res}C:${composer}\n"
        // =============================== //
        val unit = group.getUnit()
        res = "${res}L:${unit}\n"
        // =============================== //
        res = "${res}Q:${(tempo.first as Rhythm).numerator}/${(tempo.first as Rhythm).denominator}=${tempo.second}\n"
        // =============================== //
        var defaultRhythm = Rhythm(4, 4)
        if (rhythm is Rhythm) defaultRhythm = rhythm
        res = "${res}[M: ${defaultRhythm.numerator}/${defaultRhythm.denominator}]\n"
        // =============================== //
        var defaultKey = "C"
        if (key is String) defaultKey = key
        val ret = group.translate(Rhythm(1, unit), defaultKey, mutableListOf("") to mutableListOf("w:"))
        for (i in 0..ret.first.size-1) {
            res = "${res}${ret.first[i]}\n${ret.second[i]}\n"
        }
        return res
    }
}