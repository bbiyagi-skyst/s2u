package dev.jhyub.s2u.data

interface Generator<T> {
    fun generate(): List<T>
    fun evaluate(context: Map<String, Literal>)
}