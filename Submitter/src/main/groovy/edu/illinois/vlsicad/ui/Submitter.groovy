package edu.illinois.vlsicad.ui

import edu.illinois.vlsicad.core.Answer
import edu.illinois.vlsicad.core.CourseraHTTPUtils
import edu.illinois.vlsicad.core.Student
import groovy.swing.SwingBuilder

import javax.swing.*
import javax.swing.text.DefaultEditorKit
import javax.swing.text.DefaultStyledDocument
import javax.swing.undo.*
import java.awt.*

import groovy.ui.ConsoleTextEditor

import static javax.swing.WindowConstants.EXIT_ON_CLOSE

class Submitter {
    def swingBuilder // The SwingBuilder that will be used to build the application
    def frame // The main frame for the application
    def config // The config reader to read general properties

    Action loadAction, aboutAction
    Action cutAction, copyAction, pasteAction, selectAllAction // Custom action wrappers for cut/copy/paste that will have better names, bindings, etc

    UndoManager undoManager = new UndoManager()
    ConsoleTextEditor editorPane = new ConsoleTextEditor()

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

        editorPane.textEditor.setDocument(new DefaultStyledDocument()) // Disables any special syntax highlighting
        
        // Create all actions
        createMenuActions(swingBuilder)
        createEditorActions(swingBuilder)
    }

    private def createMenuActions(SwingBuilder swingBuilder) {
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

    private def createEditorActions(SwingBuilder swingBuilder) {
        def map = new JTextArea().getActionMap() // Get default actions available on all JTextPanes

        cutAction = map.get(DefaultEditorKit.cutAction)
        cutAction.putValue(Action.NAME, "Cut")
        cutAction.putValue(Action.ACCELERATOR_KEY, swingBuilder.shortcut("X"))

        copyAction = map.get(DefaultEditorKit.copyAction)
        copyAction.putValue(Action.NAME, "Copy")
        copyAction.putValue(Action.ACCELERATOR_KEY, swingBuilder.shortcut("C"))

        pasteAction = map.get(DefaultEditorKit.pasteAction)
        pasteAction.putValue(Action.NAME, "Paste")
        pasteAction.putValue(Action.ACCELERATOR_KEY, swingBuilder.shortcut("V"))

        selectAllAction = map.get(DefaultEditorKit.selectAllAction)
        selectAllAction.putValue(Action.NAME, "Select All")
        selectAllAction.putValue(Action.ACCELERATOR_KEY, swingBuilder.shortcut("A"))
    }

    private def initializeStudent() {
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
                    menu(text: 'Edit') {
                        menuItem(action: cutAction)
                        menuItem(action: copyAction)
                        menuItem(action: pasteAction)
                        separator()
                        menuItem(action: selectAllAction)

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
                        editorPane.textEditor.setText(sampleText)
                        widget(editorPane)
                        hbox(constraints: BorderLayout.PAGE_END) {
                            hglue()
                            button(text: 'Submit', actionPerformed: {
                                def submission = CourseraHTTPUtils.getChallenge(student, assignmentPart)
                                submission.answer = new Answer(answer: editorPane.text)
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
