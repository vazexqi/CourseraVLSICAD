package edu.illinois.vlsicad

import groovy.io.GroovyPrintWriter

/**
 * This example shows how to use the Groovy Process class to communicate with the kbdd command line tool. You need to
 * this installed before running this program. It captures all the stdin and stdout into one string (interleaved)
 * properly to simulate the user entering the commands and receiving feedback from bc. It does this by using the unix
 * `script` command.
 */

def INPUT_FILE = "kbddInput.txt"
def OUTPUT_FILE = "kbddOutput.txt"

def sysout = new StringBuffer() // To capture the standard output from the process
def syserr = new StringBuffer() // To capture the standard error from the process

Process proc = new ProcessBuilder("script", "--quiet", "--command", "kbdd ${INPUT_FILE}", "${OUTPUT_FILE}").start()

proc.consumeProcessOutput(sysout, syserr) // Starts two threads so that standard output and standard err can be captured

proc.waitForOrKill(20000) // Give it 20000 ms to complete or kill the process

println 'Standard out captured from process:\n' + sysout
println 'Standard err captured from process:\n' + syserr


