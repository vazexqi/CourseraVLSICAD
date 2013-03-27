package edu.illinois.vlsicad.core

import groovy.util.logging.Slf4j

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
@Slf4j
class ResilientTextDumper implements Runnable {
    final static int LIMIT = 20000
    InputStream inStream;
    Appendable app;
    int currentCount

    public ResilientTextDumper(InputStream inStream, Appendable app) {
        this.inStream = inStream;
        this.app = app;
    }

    public void run() {
        InputStreamReader isr = new InputStreamReader(inStream);
        BufferedReader br = new BufferedReader(isr);
        String next;
        try {
            while ((next = br.readLine()) != null) {
                if (app != null) {
                    if (currentCount <= LIMIT) {
                        app.append(next);
                        app.append("\n");
                        currentCount = currentCount + next.length()
                    }
                }
            }
        } catch (IOException e) {
            // Ignore because we forcibly terminate things
        }
    }
}