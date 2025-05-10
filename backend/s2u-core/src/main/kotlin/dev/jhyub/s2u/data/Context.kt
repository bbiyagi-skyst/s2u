package dev.jhyub.s2u.data

import dev.jhyub.s2u.RuntimeException

typealias Context = Map<Literal.NameLiteral, Literal>

fun Context.search(start: Literal.NameLiteral, current: Literal.NameLiteral): Literal {
    val v = this[current] ?: throw RuntimeException("Variable ${current.value} not found in context")
    println("Searching for $current from $start: got $v")
    return if (v is Literal.NameLiteral) {
        if (v.value == start.value) throw RuntimeException("Circular reference detected")
        search(start, v)
    } else {
        v
    }
}

fun Context.flatten(): Context {
    println("Flattening context")
    val x = this.toMutableMap()
    x.forEach { (t, u) ->
        if (u is Literal.NameLiteral) {
            x[t] = this.search(u, u)
        }
    }
    return x
}

fun List<NoteProperty>.applyContext(context: Context): List<NoteProperty> {
    println("Applying context $context to $this")
    return this.map {
        if (it is NoteProperty.Up && it.x is Literal.NameLiteral) {
            NoteProperty.Up(context[it.x as Literal.NameLiteral] ?: throw RuntimeException("Variable ${(it.x as Literal.NameLiteral).value} does not exist"))
        } else if (it is NoteProperty.Down && it.x is Literal.NameLiteral) {
            NoteProperty.Up(context[it.x as Literal.NameLiteral] ?: throw RuntimeException("Variable ${(it.x as Literal.NameLiteral).value} does not exist"))
        } else if (it is NoteProperty.Dynamic && it.x is Literal.NameLiteral) {
            NoteProperty.Up(context[it.x as Literal.NameLiteral] ?: throw RuntimeException("Variable ${(it.x as Literal.NameLiteral).value} does not exist"))
        } else {
            it
        }
    }
}