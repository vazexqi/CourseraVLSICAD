package edu.illinois.vlsicad.ui

import edu.illinois.vlsicad.core.Answer
import edu.illinois.vlsicad.core.CourseraHTTPUtils
import edu.illinois.vlsicad.core.Student
import groovy.swing.SwingBuilder

import java.awt.*

import static javax.swing.WindowConstants.EXIT_ON_CLOSE

class Submitter {
    def swingBuilder // The SwingBuilder that will be used to build the application
    def frame // The main frame for the application
    def config // The config reader to read general properties

    def loadAction
    def aboutAction
    def submitAction

    Student student

    // TODO: Refactor this into its own dynamically loaded module
    // Assignment specific portion
    def assignmentPart = "bc-calculator-dev"
    def sampleText = """\
a = 5
b = 4
c = a * b
c
"""

    Submitter() {
        swingBuilder = new SwingBuilder()
        config = CourseraHTTPUtils.generalConfiguration

        initializeStudent()

        // Create all actions
        loadAction = swingBuilder.action(
                name: 'Load Assignment',
                closure: this.&loadAssignment,
                mnemonic: 'O',
        )
        aboutAction = swingBuilder.action(
                name: 'About',
                closure: this.&showAbout,
                mnemonic: 'A',
        )
    }

    def initializeStudent() {
        // TODO: Read the student input from some file if possible
        student = new Student(email: "me@example.com", password: "password")
    }

    private static int WINDOW_WIDTH = 800
    private static int WINDOW_HEIGHT = 500
    private static int HALF_WIDTH = WINDOW_WIDTH / 2
    private static int QUARTER_HEIGHT = WINDOW_HEIGHT / 4
    // Builds the entire layout using SwingBuilder DSL
    void buildLayout() {
        swingBuilder.edt {
            lookAndFeel 'nimbus'
            frame = swingBuilder.frame(title: config.window.title,
                    minimumSize: [WINDOW_WIDTH, WINDOW_HEIGHT],
                    defaultCloseOperation: EXIT_ON_CLOSE) {

                menuBar() {
                    menu(text: 'File') {
                        menuItem(action: loadAction)
                        menuItem(text: 'Quit', actionPerformed: { event -> dispose() })
                    }
                    menu(text: 'Help') {
                        menuItem(action: aboutAction)
                    }
                }
                vbox(constraints: BorderLayout.LINE_START) {
                    panel(border: compoundBorder([emptyBorder(1), titledBorder('Enter your information')]),
                            preferredSize: [HALF_WIDTH, QUARTER_HEIGHT],
                            maximumSize: [HALF_WIDTH, QUARTER_HEIGHT]) {
                        tableLayout {
                            tr {
                                td { label 'Username:' }
                                td { textField text: bind(source: student, sourceProperty: 'email', mutual: true), id: 'username', columns: 20 }
                            }
                            tr {
                                td { label 'Assignment password:' }
                                td { textField text: bind(source: student, sourceProperty: 'password', mutual: true), id: 'password', columns: 20 }
                            }
                        }
                    }
                    panel(border: compoundBorder([titledBorder('Complete the submission')]),
                            preferredSize: [HALF_WIDTH, 3 * QUARTER_HEIGHT]) {
                        borderLayout()
                        scrollPane(constraints: BorderLayout.CENTER) { textArea(id: 'bcCommands', text: sampleText) }
                        hbox(constraints: BorderLayout.PAGE_END) {
                            hglue()
                            button(text: 'Submit', actionPerformed: {
                                def submission = CourseraHTTPUtils.getChallenge(student, assignmentPart)
                                submission.answer = new Answer(answer: swingBuilder."bcCommands".text)
                                def response = submission.submit()

                                def currentTime = new Date().timeString
                                def currentResult = swingBuilder."results".text
                                swingBuilder."results".text = currentResult + System.getProperty("line.separator") + currentTime + System.getProperty("line.separator") + response
                            }
                            )
                        }

                    }
                }
                vbox(constraints: BorderLayout.CENTER) {
                    panel(border: compoundBorder([emptyBorder(1), titledBorder('Results')])) {
                        borderLayout()
                        scrollPane { textArea(id: 'results') }
                        hbox(constraints: BorderLayout.PAGE_END) {
                            hglue()
                            button 'Refresh'
                        }
                    }
                }
            }
        }
    }

    void loadAssignment(event) {

    }

    void showAbout(event) {

    }

    void show() {
        frame.show()
    }

    public static void main(String[] args) {
        def submitter = new Submitter()
        submitter.buildLayout()
        submitter.show();
    }

}
