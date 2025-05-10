package dev.jhyub.s2y.data

class Sheet(
    val title: String,
    val composer: String,
    val group: CalledGroup,
    val rhythm: Rhythm?,
    val tempo: Pair<String?, Int?>,
    val key: String?,
    val clef: Clef?,
    val pos: CodePosition
) {
    fun evaluate() {
        group.evaluate(mapOf())
    }
}