package edu.illinois.vlsicad.assignment.bc

import edu.illinois.vlsicad.core.Grade
import edu.illinois.vlsicad.core.Grader
import edu.illinois.vlsicad.core.Submission

class BCGrader extends Grader {

    @Override
    def grade(Submission submission) {
        println "Student submission\n" + "${submission.answer.answer}"

        def sout = new StringBuilder()
        def serr = new StringBuilder()

        Process proc = "bc -q".execute()

        proc.consumeProcessOutput(sout, serr)

        proc.withWriter { writer ->
            def rawAnswer = submission.answer.answer
            // Converts the answer (in LR, CR/LF, CR) into one terminated by the platform specific line separator.
            def platformSpecificNewlineAnswer = rawAnswer.denormalize()
            writer << platformSpecificNewlineAnswer
        }

        def feedback = new StringBuilder()
        feedback << "Standard output\n"
        feedback << sout.toString()
        feedback << "\n"
        feedback << "Standard error\n"
        feedback << serr.toString()
        feedback << "\n"

        return new Grade(score: 1, feedback: feedback as String, apiState: submission.apiState)

    }

    @Override
    def queueName() {
        "bc-calculator"
    }
}
