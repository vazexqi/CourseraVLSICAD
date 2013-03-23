package edu.illinois.vlsicad.core

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Writer
import java.util.List

/**
 * This is a version of the TextDumper from
 * https://github.com/groovy/groovy-core/blob/master/src/main/org/codehaus/groovy/runtime/ProcessGroovyMethods.java
 *
 * The original file is licensed under an Apache 2 License.
 *
 * The only change is that we don't throw a GroovyRuntimeException on IOException in the catch block. In fact, the
 * IOEXception is to be expected since we forcibly terminate streams upon timeout.
 *
 */
class ResilientTextDumper implements Runnable {
    InputStream inStream
    Appendable app

    public ResilientTextDumper(InputStream inStream, Appendable app) {
        this.inStream = inStream
        this.app = app
    }

    public void run() {
        InputStreamReader isr = new InputStreamReader(inStream)
        BufferedReader br = new BufferedReader(isr)
        String next
        try {
            while ((next = br.readLine()) != null) {
                if (app != null) {
                    app.append(next)
                    app.append("\n")
                }
            }
        } catch (IOException e) {
            // Ignore because we forcibly terminate things
        }
    }
}