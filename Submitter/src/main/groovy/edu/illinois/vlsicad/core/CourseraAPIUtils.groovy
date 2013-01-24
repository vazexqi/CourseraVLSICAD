package edu.illinois.vlsicad.core

import groovyx.net.http.HTTPBuilder

import java.security.MessageDigest

/**
 * A direct port of the python example from
 * - http://support.coursera.org/customer/portal/articles/573466-programming-assignments
 */
class CourseraAPIUtils {
    static final String GENERAL_PROPERTIES_FILE = 'resources/config.groovy'
    static generalConfiguration = new ConfigSlurper().parse(new File(GENERAL_PROPERTIES_FILE).toURI().toURL())

    /**
     * Gets the challenge salt from the server. Returns a partially filled Submission object based on the server response
     * @param student A student object with valid email
     * @param assignmentPart The assignment part as declared on the assignment page
     */
    static getChallenge(student, assignmentPart) {
        def http = new HTTPBuilder(challengeURL())
        def values = ['email_address': student.email, 'assignment_part_sid': assignmentPart, 'response_encoding': 'delim']
        def response = http.post(body: values)
        def text = response.toString().trim()

        def splits = text.split("\\|", -1) // Because challenge_auxiallary might be empty, we need this special version of split to not discard the data
        if (splits.length != 9) {
            System.err << "Badly formatted challenge response: ${text}"
            return new NullSubmission(status: text)
        }
        return new Submission(student: student, assignmentPart: assignmentPart, challenge: splits[4], state: splits[6], challengeAuxillary: splits[8])
    }

    /**
     * @return The challenge URL
     */
    static challengeURL() {
        "https://class.coursera.org/" + generalConfiguration.core.course.session + "/assignment/challenge"
    }

    /**
     * @return The submission URL
     */
    static submitURL() {
        "https://class.coursera.org/" + generalConfiguration.core.course.session + "/assignment/submit"
    }
}

/**
 * A submission for a particular student for a particular part of the assignment
 */
class Submission {
    Student student // Student's identity
    Answer answer // Student's answers
    String assignmentPart, challenge, challengeAuxillary, challengeResponse, state // Filled in by the system

    def submit() {
        prepareForSubmission()
        def response = submitThroughHTTP()
        response.toString().trim()
    }

    private def submitThroughHTTP() {
        def http = new HTTPBuilder(CourseraAPIUtils.submitURL())
        def values = ['assignment_part_sid': assignmentPart,
                'email_address': student.email,
                'submission': answer.answerBase64,
                'submission_aux': answer.additionalDataBase64,
                'challenge_response': challengeResponse,
                'state': state]

        http.post(body: values)
    }

    private def prepareForSubmission() {
        challengeResponse = respondToChallenge()
        answer.encode()
    }

    private def respondToChallenge() {
        MessageDigest digest = MessageDigest.getInstance("SHA1")
        digest.update((challenge + student.password).getBytes())
        return new BigInteger(1, digest.digest()).toString(16)
    }
}

/**
 * A null submission that doesn't do anything
 */
class NullSubmission extends Submission {
    String status

    def submit() {
       // Does nothing but returns the error status from earlier
       return status
    }
}

/**
 * Represents a student identity for this assignment. The password is assignment specific
 */
class Student {
    String email, password
}

/**
 * Represents an answer for a particular part of the assignment. There are two portions, the main answer and an additional
 * field for other data (e.g., source files)
 */
class Answer {
    byte[] answer = "", additionalData = ""
    String answerBase64, additionalDataBase64

    def encode() {
        answerBase64 = answer.encodeBase64()
        additionalDataBase64 = additionalData.encodeBase64()
    }
}
