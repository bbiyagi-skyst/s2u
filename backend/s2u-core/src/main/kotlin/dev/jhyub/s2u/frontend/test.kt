package dev.jhyub.s2u.frontend


fun main() {
    val source =
        """
            sheet {
                title: "그대에게"
                s1()
                @u(1) s1(x)
                @s s1(2)
                s()
            }
            """.trimIndent()
    /*
            sec s() {
                loop(10) { i ->
                    s1(i)
                }
            }
            
            sec s1(x) {
                rhythm: 4/4
                tempo: 1/4, 80
                key: Cm
                clef: High
                loop(2) {
                    [:
                        c: "Cm7 - - "
                        n: C + F, 1/4, u(x)
                        n: E3, 1/4, s, d(F3)
                        n: R, 1/2
                    ]
                }
                [
                    n: F, 1
                :]
                [
                    (
                    n: F, 1/4
                    n: G, 1/4
                    n: A, 1/4
                    )
                    n: E, 1/4
                ]
                [
                (3
                    n: E, 1/4
                    n: G, 1/8
                )
                ]
            }
        """.trimIndent()
            */


    println(source.tokenize())

    val file = File.fromTokens(source.tokenize())
    println(file)

}