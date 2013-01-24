package edu.illinois.vlsicad.core

import groovyx.net.http.HTTPBuilder

/**
 * A direct port of the python example from
 * - http://support.coursera.org/customer/portal/articles/573466-programming-assignments
 */
class Grader {
    static final String GENERAL_PROPERTIES_FILE = 'resources/config.groovy'
    static config = new ConfigSlurper().parse(new File(GENERAL_PROPERTIES_FILE).toURI().toURL())

    /**
     * Grades the assignment and constructs a new Grade object with the score and feedback
     * This part is assignment specific and the code below is just a port of the Coursera example
     * @param submission The student submission to grade
     */
    def grade(Submission submission) {
        println "Official Solution: ${submission.answer.solutions}"
        println "Student submission ${submission.answer.answer}"

        def solutions = submission.answer.solutions.split("\\r?\\n")
        def submissions = submission.answer.answer.split("\\r?\\n")
        def numCorrect = 0

        solutions.eachWithIndex { String entry, int i ->
            def currentSubmission = submissions[i].split(',')
            if (currentSubmission.length == 2) {
                if ((currentSubmission[0] as int) + (currentSubmission[1] as int) == (entry as int))
                    numCorrect++
            }
        }
        float score = numCorrect * 2
        def feedback = ""

        switch (score) {
            case 10:
                feedback = "Congratulations! You got it right!"
                break
            case 0:
                feedback = "Sorry, your answer was incorrect."
                break
            default:
                feedback = "Almost there! You got ${numCorrect} out of 5 test cases right."
        }

        return new Grade(score: score, feedback: feedback, apiState: submission.apiState)
    }

    /**
     * Requests a submission from the Coursera API. This will return a submission that has not been graded yet.
     * See http://support.coursera.org/customer/portal/articles/573466-server-side-execution-external-custom-graders
     * @return The JSON representation of the submission
     */
    def poll() {
        def http = new HTTPBuilder(pollURL())
        def api = ['x-api-key': config.core.coursera.API, 'Accept': 'application/json,text/html']
        // Must force HTTPBuilder to parse as JSON since the content-type header returned by Coursera
        // wrongfully says that the response is of text/html
        def json = http.get(headers: api, contentType: 'application/json')
        def submission = (json.submission) ? new Submission(json) : null
        return submission
    }

    /**
     * Posts the grade back to Coursera
     * @param grade The grade to post
     */
    def post(Grade grade) {
        def http = new HTTPBuilder(postURL())
        def api = ['x-api-key': config.core.coursera.API]
        def values = ['api_state': grade.apiState,
                        'score': grade.score,
                        'feedback': grade.feedback,
                        'feedback_after_hard_deadline': grade.feedbackAfterHardDeadline,
                        'feedback_after_soft_close_time': grade.feedbackAfterSoftCloseTime]
        http.post(headers: api, body:values)
    }

    def postURL() {
        "https://class.coursera.org/" + config.core.course.session + "/assignment/api/score"
    }

    def pollURL() {
        "https://class.coursera.org/" + config.core.course.session + "/assignment/api/pending_submission?queue=" + queueName()
    }

    def queueName() {
        "course_382_queue_splitsums"
    }
}

class Submission {
    String apiState
    Answer answer // Solutions from the students
    String submissionTime

    Submission(json) {
        def submission = json.submission

        apiState = submission.api_state

        answer = new Answer(answerBase64: submission.submission, additionalDataBase64: submission.submission_aux, solutions: submission.solutions)
        answer.decode()

        submissionTime = submission.submission_metadata.submission_time
    }
}

/**
 * Represents an answer for a particular part of the assignment. There are two portions, the main answer and an additional
 * field for other data (e.g., source files)
 */
class Answer {
    String answer, additionalData
    String answerBase64, additionalDataBase64
    String solutions // Answers specified on the assignment page, if any

    def decode() {
        answer = new String(answerBase64.decodeBase64())
        additionalData = new String(additionalDataBase64.decodeBase64())
    }
}

/**
 * Represents a grade to the student with score and feedback
 */
class Grade {
    String apiState
    float score
    String feedback = "", feedbackAfterSoftCloseTime = "", feedbackAfterHardDeadline = ""
}
