package edu.illinois.vlsicad.assignment.kbdd

import edu.illinois.vlsicad.core.Grade
import edu.illinois.vlsicad.core.Grader
import edu.illinois.vlsicad.core.Submission
import groovy.io.GroovyPrintWriter
import groovy.xml.MarkupBuilder

class KBDDGrader extends Grader {
    File inputFile
    File outputFile

    File initializeTempFile(String fileName, String contents = null) {
        File file = File.createTempFile(fileName, '.vlsiTmp')
        if (contents != null) {
            def gWriter = new GroovyPrintWriter(file)

            contents.eachLine { line ->
                gWriter.println line.denormalize()
            }
            gWriter.flush()
        }
        file.deleteOnExit()
        return file
    }

    @Override
    def grade(Submission submission) {
        inputFile = initializeTempFile("inputFile", submission.answer.answer)
        outputFile = initializeTempFile("outputFile")

        def sout = new StringBuilder()
        def serr = new StringBuilder()

        Process proc = new ProcessBuilder("script", "--quiet", "--command", "${config.kbdd.location} ${inputFile.getAbsolutePath()}", "${outputFile.getAbsolutePath()}").start()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill(20000)

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
        "course_382_queue_kbdd-interface"
    }
}

