package dev.jhyub.s2u.data

class Loop<T>(
    val times: Int,

): Generator<T> {
    override fun generate(): List<T> {
        TODO("Not yet implemented")
    }

    override fun evaluate(context: Map<String, Literal>) {
        TODO("Not yet implemented")
    }

}