package edu.illinois.vlsicad.ui

import groovy.swing.SwingBuilder

import java.awt.*

import static javax.swing.WindowConstants.EXIT_ON_CLOSE

class Submitter {
    private static final String PROPERTIES_FILE = 'resources/properties.groovy'

    def swing // The SwingBuilder that will be used to build the application
    def frame // The main frame for the application
    def config // The config reader to read general properties

    def loadAction
    def aboutAction

    Submitter() {
        swing = new SwingBuilder()
        config = new ConfigSlurper().parse(new File(PROPERTIES_FILE).toURI().toURL())

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

    // Builds the entire layout using SwingBuilder DSL
    void buildLayout() {
        swing.edt {
            lookAndFeel 'nimbus'
            frame = swing.frame(title: config.window.title,
                    location: [100, 100],
                    minimumSize: [500, 500],
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
                vbox(size: [240, 450], constraints: BorderLayout.LINE_START, border: lineBorder(color: Color.RED)) {
                    panel(border: compoundBorder([emptyBorder(10), titledBorder('Enter your information')])) {
                        tableLayout {
                            tr {
                                td { label 'Username:' }
                                td { textField text: 'me@example.com', id: 'username', columns: 20 }
                            }
                            tr {
                                td { label 'Assignment Password' }
                                td { textField text: 'password', id: 'password', columns: 20 }
                            }
                        }
                    }
                    panel(size: [240, 200], border: compoundBorder([emptyBorder(10), titledBorder('Complete the submission')])) {
                        scrollPane { textArea() }
                    }
                }
                vbox(size: [240, 450], constraints: BorderLayout.CENTER, border: lineBorder(color: Color.RED)) {
                    panel(border: compoundBorder([emptyBorder(10), titledBorder('Results')])) {}
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
