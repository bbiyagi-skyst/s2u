package dev.jhyub.s2y.data

interface Generator<T> {
    fun generate(): List<T>
}