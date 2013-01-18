package edu.illinois.vlsicad.ui

import edu.illinois.vlsicad.core.CourseraAPIUtils
import groovy.swing.SwingBuilder

import java.awt.*

import static javax.swing.WindowConstants.EXIT_ON_CLOSE

class Submitter {
    def swing // The SwingBuilder that will be used to build the application
    def frame // The main frame for the application
    def config // The config reader to read general properties

    def loadAction
    def aboutAction

    Submitter() {
        swing = new SwingBuilder()
        config = new ConfigSlurper().parse(new File(CourseraAPIUtils.PROPERTIES_FILE).toURI().toURL())

        // Create all actions
        loadAction = swing.action(
                name: 'Load Assignment',
                closure: this.&loadAssignment,
                mnemonic: 'O',
        )
        aboutAction = swing.action(
                name: 'About',
                closure: this.&showAbout,
                mnemonic: 'A',
        )
    }

    private static int WINDOW_WIDTH = 800
    private static int WINDOW_HEIGHT = 500
    private static int HALF_WIDTH = WINDOW_WIDTH / 2
    private static int QUARTER_HEIGHT = WINDOW_HEIGHT / 4
    // Builds the entire layout using SwingBuilder DSL
    void buildLayout() {
        swing.edt {
            lookAndFeel 'nimbus'
            frame = swing.frame(title: config.window.title,
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
                vbox(constraints: BorderLayout.LINE_START, border: lineBorder(color: Color.RED)) {
                    panel(border: compoundBorder([emptyBorder(1), titledBorder('Enter your information')]),
                            preferredSize: [HALF_WIDTH, QUARTER_HEIGHT],
                            maximumSize: [HALF_WIDTH, QUARTER_HEIGHT]) {
                        tableLayout {
                            tr {
                                td { label 'Username:' }
                                td { textField text: 'me@example.com', id: 'username', columns: 20 }
                            }
                            tr {
                                td { label 'Assignment password:' }
                                td { textField text: 'password', id: 'password', columns: 20 }
                            }
                        }
                    }
                    panel(border: compoundBorder([lineBorder(color: Color.RED), titledBorder('Complete the submission')]),
                            preferredSize: [HALF_WIDTH, 3 * QUARTER_HEIGHT]) {
                        borderLayout()
                        scrollPane(border: lineBorder(color: Color.RED)) { textArea() }
                    }
                }
                vbox(constraints: BorderLayout.CENTER, border: lineBorder(color: Color.RED)) {
                    panel(border: compoundBorder([emptyBorder(1), titledBorder('Results')])) {
                        borderLayout()
                        scrollPane { textArea() }
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
