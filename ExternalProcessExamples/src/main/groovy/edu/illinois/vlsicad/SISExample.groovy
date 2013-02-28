package edu.illinois.vlsicad

import groovy.io.GroovyPrintWriter

/**
 * This example shows how to capture the output from running sis. The sis interface is
 * a command line prompt. The easiest way to interface with sis is to use a combination of putting stuff
 * into a input file (then using read_pla to read it). And then specifying some (minimal) commands through STDIN.
 */

// You might need to change this if SIS is not installed in the default locations
def SIS_LOCATION = '/home/ubuntu/bin/sis'

def sampleInputString = """
.i 6
.o 4
.ilb a2 a1 a0 b2 b1 b0
.ob s3 s2 s1 s0
000000 0000
000001 0000
000010 0000
000011 0000
000100 0000
000101 0000
000110 0000
000111 0000
001000 0000
001001 0001
001010 0010
001011 0011
001100 0100
001101 0101
001110 0110
001111 0111
010000 0000
010001 0010
010010 0100
010011 0110
010100 1000
010101 1010
010110 1100
010111 0001
011000 0000
011001 0011
011010 0110
011011 1001
011100 1100
011101 0010
011110 0101
011111 1000
100000 0000
100001 0100
100010 1000
100011 1100
100100 0011
100101 0111
100110 1011
100111 0010
101000 0000
101001 0101
101010 1010
101011 0010
101100 0111
101101 1100
101110 0100
101111 1001
110000 0000
110001 0110
110010 1100
110011 0101
110100 1011
110101 0100
110110 1010
110111 0011
111000 0000
111001 0111
111010 0001
111011 1000
111100 0010
111101 1001
111110 0011
111111 1010
.e
"""

// The easiest way is actually to require two levels of file generation ;-)
def inputFile = File.createTempFile('inputfile', '.vlsiTmp')
inputFile.with { file ->
    def gWriter = new GroovyPrintWriter(file)

    // Populate with contents of the input string
    sampleInputString.eachLine { line ->
        gWriter.println line.denormalize()
    }
    gWriter.flush()
    file.deleteOnExit()
}

def commandFile = File.createTempFile('commandfile', '.vlsiTmp')
commandFile.with { file ->
    def gWriter = new GroovyPrintWriter(file)

    gWriter.println "read_pla ${inputFile.getAbsolutePath()}"
    // This is the special sequence of SIS command called the "rugged script"
    gWriter.println "sweep; eliminate -1"
    gWriter.println "simplify -m nocomp"
    gWriter.println "eliminate -1"
    gWriter.println "sweep; eliminate 5"
    gWriter.println "simplify -m nocomp"
    gWriter.println "resub -a"
    gWriter.println "fx"
    gWriter.println "resub -a; sweep"
    gWriter.println "eliminate -1; sweep"
    gWriter.println "full_simplify -m nocomp"
    gWriter.println "print"
    gWriter.println "quit"

    gWriter.flush()
    file.deleteOnExit()
}

def sout = new StringBuffer() // To capture the standard output from the process (outputs, etc)
def serr = new StringBuffer() // To capture the standard error from the process (parsing errors, etc)

Process proc = "${SIS_LOCATION} -x -f ${commandFile.getAbsolutePath()}".execute() // Starts the sis CLI with -q to suppress its verbose welcome message

proc.waitForProcessOutput(sout, serr) // Starts two threads so that standard output and standard err can be captured

proc.waitForOrKill(20000) // Give it 20000 ms to complete or kill the process

println 'Standard out captured from process:\n ' + sout
println 'Standard err captured from process:\n ' + serr
