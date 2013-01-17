package edu.illinois.vlsicad.ui

import groovy.swing.SwingBuilder

import javax.swing.WindowConstants as WC

class Submitter {
    def swing; // The SwingBuilder that will be used to build the application
    def frame; // The main frame for the application
    def config; // The config reader to read general properties

    Submitter() {
        swing = new SwingBuilder()
        config = new ConfigSlurper().parse(new File('resources/properties.groovy').toURI().toURL())
    }

    void buildLayout() {
        frame = swing.frame(title: config.window.title,
                location: [100, 100],
                size: [300, 300],
                defaultCloseOperation: WC.EXIT_ON_CLOSE)
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
