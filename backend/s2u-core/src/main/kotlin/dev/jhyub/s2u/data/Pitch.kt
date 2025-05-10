package dev.jhyub.s2u.data

class Pitch(val name: PitchName, val octave: Int) {
}

enum class PitchName {
    C, D, E, F, G, A, B
}