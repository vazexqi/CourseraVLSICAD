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
    def poll(){
        def http = new HTTPBuilder(pollURL())
        def api = ['x-api-key': config.core.coursera.API]
        def json = http.get(headers: api)
        return json
    }

    def pollURL() {
        "https://class.coursera.org/" + config.core.course.session + "/assignment/api/pending_submission?queue=" + queueName()
    }

    def queueName() {
        "course_382_queue_splitsums"
    }
}

class Submission {

}
