package dev.jhyub.s2u.data

interface Generator<T> {
    fun generate(context: Context, annotation: List<NoteProperty>): List<T>
}