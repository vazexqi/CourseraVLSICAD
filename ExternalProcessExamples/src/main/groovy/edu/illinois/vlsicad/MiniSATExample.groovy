package edu.illinois.vlsicad

import groovy.io.GroovyPrintWriter

/**
 * This example shows how to capture the output from running miniSAT. The miniSAT interface is
 * minisat input_file output_file. Since our input is not in a file format, we create a temporary file
 * to hold its contents and then pipe it to miniSAT. We also need to grab the contents of the output_file
 * since the results (the solution) is not printed to STDOUT/STDERR.
 */

// You might need to change this if miniSAT is not installed in the default locations
def MINISAT_LOCATIION = '/usr/bin/minisat'

def sampleInputString = """
c Comment line begins by 'c'
c This is second comment line
p cnf 3 2
1 -3 0
2 3 -1 0
"""
def inputFile = File.createTempFile('inputfile', '.vlsiTmp')
inputFile.with { file ->
    def gWriter = new GroovyPrintWriter(file)

    // Populate with contents of the input string
    sampleInputString.eachLine { line ->
        gWriter.println line
    }
    gWriter.flush()
    file.deleteOnExit()
}

def outputFile = File.createTempFile('outputFile', '.vlsiTmp')
outputFile.deleteOnExit()

def sout = new StringBuffer() // To capture the standard output from the process (statistics, etc)
def serr = new StringBuffer() // To capture the standard error from the process (parsing errors, etc)

Process proc = "${MINISAT_LOCATIION} -verb=0 ${inputFile.getAbsolutePath()} ${outputFile.getAbsolutePath()}".execute() // Starts the bc CLI with -q to suppress its verbose welcome message

proc.consumeProcessOutput(sout, serr) // Starts two threads so that standard output and standard err can be captured

proc.waitForOrKill(1000) // Give it 1000 ms to complete or kill the process

// Read the contents of file as a text string
def results = outputFile.text

println 'Standard out captured from process:\n ' + sout
println 'Standard err captured from process:\n ' + serr
println 'Results of miniSAT: ' + results


