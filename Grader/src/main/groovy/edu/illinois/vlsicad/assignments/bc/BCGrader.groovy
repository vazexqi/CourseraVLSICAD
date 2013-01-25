package edu.illinois.vlsicad.assignments.bc

import edu.illinois.vlsicad.core.*

class BCGrader extends Grader {

    @Override
    def grade(Submission submission) {
        println "Student submission\n" + "${submission.answer.answer}"

        def sout = new StringBuilder()
        def serr = new StringBuilder()

        Process proc = "bc -q".execute()

        proc.consumeProcessOutput(sout, serr)

        proc.withWriter {writer ->
            writer << submission.answer.answer
        }

        proc.waitForOrKill(20000)

        def feedback = new StringBuilder()
        feedback << "Standard output\n"
        feedback << sout.toString()
        feedback << "\n"
        feedback << "Standard error\n"
        feedback << serr.toString()
        feedback << "\n"

        return new Grade(score:1, feedback: feedback as String, apiState: submission.apiState)

    }

    @Override
    def queueName() {
        "course_382_queue_bc-calculator"
    }
}
