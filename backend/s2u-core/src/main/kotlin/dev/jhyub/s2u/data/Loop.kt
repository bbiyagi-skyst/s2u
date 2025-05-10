package dev.jhyub.s2u.data

data class Loop<T: Generator<V>, V>(
    val start: Int = 0,
    val end: Int,
    val step: Int = 1,
    val indexName: String = "i",
    val generators: List<T>
): Generator<V> {
    override fun generate(context: Context, annotation: List<NoteProperty>): List<V> {
        return ((if (start < end) { start..<end } else { start downTo end+1 }) step step).map { i ->
            generators.map { v ->
                val newContext = context.toMutableMap()
                newContext.put(Literal.NameLiteral(indexName), Literal.IntLiteral(i))
                val context = newContext.flatten()
                v.generate(context, annotation)
            }.flatten()
        }.flatten()
    }
}