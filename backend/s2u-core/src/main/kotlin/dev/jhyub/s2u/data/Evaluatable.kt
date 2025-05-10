package dev.jhyub.s2u.data

// Make a clone by filling in context
interface Evaluatable<T> {
    fun evaluate(context: Context, annotation: List<NoteProperty>): T
}