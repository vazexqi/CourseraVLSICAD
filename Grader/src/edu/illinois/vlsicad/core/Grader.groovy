package edu.illinois.vlsicad.core

import groovyx.net.http.HTTPBuilder

/**
 * A direct port of the python example from
 * - http://support.coursera.org/customer/portal/articles/573466-programming-assignments
 */
class Grader {
    static final String GENERAL_PROPERTIES_FILE = 'resources/config.groovy'
    static config = new ConfigSlurper().parse(new File(GENERAL_PROPERTIES_FILE).toURI().toURL())

    def gradeSubmissions() {
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
    Submission(json) {
        def submission = json.submission

        apiState = submission.api_state

        answer = new Answer(answerBase64: submission.submission, additionalDataBase64: submission.submission_aux, solutions: submission.solutions)
        answer.decode()
    }
}

/**
 * Represents an answer for a particular part of the assignment. There are two portions, the main answer and an additional
 * field for other data (e.g., source files)
 */
class Answer {
    byte[] answer, additionalData
    String answerBase64, additionalDataBase64
    String solutions // Answers specified on the assignment page, if any

    def decode() {
        answer = answerBase64.decodeBase64()
        additionalData = additionalDataBase64.decodeBase64()
    }
}

/**
 * Represents a grade to the student with score and feedback
 */
class Grade {
    String apiState
    float score
    String feedback, feedbackAfterSoftCloseTime, feedbackAfterHardCloseTime
}
