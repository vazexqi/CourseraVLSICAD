package edu.illinois.vlsicad.core

import groovyx.net.http.HTTPBuilder

/**
 * A direct port of the python example from
 * - http://support.coursera.org/customer/portal/articles/573466-programming-assignments
 */
class CourseraAPIUtils {

    public static final String PROPERTIES_FILE = 'resources/properties.groovy'
    static config = new ConfigSlurper().parse(new File(PROPERTIES_FILE).toURI().toURL())

    /**
     * Gets the challenge salt from the server. Returns a map of [email: ?, ch: ?, state: ?, ch_aux: ?]
     * @param email Student's e-mail
     * @param assignment_part The assignmet part as declared on the assignment page
     */
    static getChallenge(email, assignment_part) {
        def http = new HTTPBuilder(challenge_url());
        def values = ['email_address': email, 'assignment_part_sid': assignment_part, 'response_encoding': 'delim']
        def response = http.post(body: values)
        def text = response.toString().trim()

        def splits = text.split("\\|", -1) // Because challenge_auxiallary might be empty, we need this special version of split to not discard the data
        if (splits.length != 9) {
            System.err << "Badly formatted challenge response: " + text
            return []
        }
        return [email: splits[2], ch: splits[4], state: splits[6], ch_aux: splits[8]]
    }

    /**
     * @return The challenge URL
     */
    static challenge_url() {
        return "https://class.coursera.org/" + config.core.course.session + "/assignment/challenge"
    }

}
