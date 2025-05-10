package dev.jhyub.s2u.data

data class Pitch(val name: PitchName, val octave: Int) {
    operator fun plus(semitones: Int): Pitch {
        if (name == PitchName.R) {
            return this
        }

        val pitchToInt = mapOf(
            PitchName.C  to 0,
            PitchName.Db to 1,
            PitchName.D  to 2,
            PitchName.Eb to 3,
            PitchName.E  to 4,
            PitchName.F  to 5,
            PitchName.Gb to 6,
            PitchName.G  to 7,
            PitchName.Ab to 8,
            PitchName.A  to 9,
            PitchName.Bb to 10,
            PitchName.B  to 11
        )
        val intToPitch = mapOf(
            0  to PitchName.C,
            1  to PitchName.Db,
            2  to PitchName.D,
            3  to PitchName.Eb,
            4  to PitchName.E,
            5  to PitchName.F,
            6  to PitchName.Gb,
            7  to PitchName.G,
            8  to PitchName.Ab,
            9  to PitchName.A,
            10 to PitchName.Bb,
            11 to PitchName.B
        )

        assert(pitchToInt[name] != null)

        val result = (pitchToInt[name]!!.toInt() + semitones).mod(12)
        val octaveDelta = (pitchToInt[name]!!.toInt() + semitones) / 12
        return Pitch(intToPitch[result]!!, octave + octaveDelta)
    }

    fun translate(): String {
        // C4 -> C in abcJS
        // B4 -> B in abcJS
        // C5 -> C' in abcJS
        var result = name.name;
        if (result == "R") {
            result = "z"
        }
        else if (octave <= 4) {
            // , * (4 - octave)
            for (i in 1..(4-octave)) {
                result = "$result,"
            }
        }
        else {
            // ' * (octave - 4)
            for (i in 1..(octave-4)) {
                result = "$result'"
            }
        }
        return result
    }
}

enum class PitchName {
    C, Db, D, Eb, E, F, Gb, G, Ab, A, Bb, B, R
}

//fun main() {
//    println(Pitch(PitchName.E, 6).translate());
//}