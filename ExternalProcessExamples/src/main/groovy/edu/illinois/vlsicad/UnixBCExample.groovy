package edu.illinois.vlsicad

/**
 * This example shows how to use the Groovy Process class to communicate with the BC command line calculator on *nix
 * platforms.
 */

def sout = new StringBuffer() // To capture the standard output from the process
def serr = new StringBuffer() // To capture the standard error from the process

Process proc = "bc -q".execute() // Starts the bc CLI with -q to suppress its verbose welcome message

proc.consumeProcessOutput(sout, serr) // Starts two threads so that standard output and standard err can be captured

proc.withWriter {writer ->
    writer << "a = 4\n"
    writer << "b = 5\n"
    writer << "c = b * a\n"
    writer << "c\n"
    writer << "quit\n"
}
proc.waitForOrKill(1000) // Give it 1000 ms to complete or kill the process

println 'Standard out captured from process: ' + sout
println 'Standard err captured from process: ' + serr

assert 20 == sout as int

