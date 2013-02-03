package edu.illinois.vlsicad.initialization

/**
 * This writes to STDOUT an espresso compatible truth table for the following computation on two 3-bit unsigned numbers
 * and produces a 4-bit unsigned number:
 * s[3:0] = (a[2:0] * b[2:0]) MOD 13
 */

println '.i 6' // 6 input bits
println '.o 4' // 4 output bits
println '.ilb a2 a1 a0 b2 b1 b0' // names of inputs
println '.ob s3 s2 s1 s0' /// names of outputs

// Print the truth table
(0..<8).each { a ->
    (0..<8).each { b ->
        def c = (a * b) % 13
        print Integer.toString(a, 2).padLeft(3, '0')
        print Integer.toString(b, 2).padLeft(3, '0')
        print ' ' // Needs a space between the inputs and output
        println Integer.toString(c, 2).padLeft(4, '0')
    }
}


println '.e' // done