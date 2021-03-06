package edu.illinois.vlsicad.assignment.kbdd

import edu.illinois.vlsicad.core.Grade
import edu.illinois.vlsicad.core.Grader
import edu.illinois.vlsicad.core.Submission
import groovy.io.GroovyPrintWriter
import groovy.xml.MarkupBuilder

class KBDDGrader extends Grader {
    File inputFile
    File outputFile

    @Override
    def grade(Submission submission) {
        inputFile = initializeTempFile("inputFile", submission.answer.answer)
        outputFile = initializeTempFile("outputFile")

        def sout = new StringBuilder()
        def serr = new StringBuilder()

        Process proc = new ProcessBuilder("script", "--quiet", "--command", "${config.kbdd.location} ${inputFile.getAbsolutePath()}", "${outputFile.getAbsolutePath()}").start()
        proc.consumeProcessOutput(sout, serr)

        def writer = new StringWriter()
        def markupBuilder = new MarkupBuilder(writer)
        markupBuilder.div() {
            h4("Standard output")
            br()

            sout.eachLine { line ->
                span(line)
                br()
            }

            br()

            h4("Standard error")
            br()

            serr.eachLine { line ->
                span(line)
                br()
            }
        }

        return new Grade(score: 1, feedback: writer.toString(), apiState: submission.apiState)
    }

    @Override
    def queueName() {
        "kbdd-interface"
    }
}

