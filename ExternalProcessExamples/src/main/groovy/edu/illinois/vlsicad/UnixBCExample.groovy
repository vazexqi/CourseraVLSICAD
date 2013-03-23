package edu.illinois.vlsicad

import groovy.io.GroovyPrintWriter

/**
 * This example shows how to use the Groovy Process class to communicate with the BC command line calculator on *nix
 * platforms.
 */

def sout = new StringBuffer() // To capture the standard output from the process
def serr = new StringBuffer() // To capture the standard error from the process

Process proc = "bc -q".execute() // Starts the bc CLI with -q to suppress its verbose welcome message

proc.waitForProcessOutput(sout, serr) // Starts two threads so that standard output and standard err can be captured

proc.withWriter {writer ->
    def gWriter = new GroovyPrintWriter(writer)
    writer.println "a = 4"
    writer.println "b = 5"
    writer.println "c = b * a"
    writer.println "c"
    writer.println "quit"
}

println 'Standard out captured from process: ' + sout
println 'Standard err captured from process: ' + serr

assert 20 == sout as int

