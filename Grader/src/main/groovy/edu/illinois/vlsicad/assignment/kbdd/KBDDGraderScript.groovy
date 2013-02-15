package edu.illinois.vlsicad.assignment.kbdd

while (true) {
    def grader = new KBDDGrader();
    def submission = grader.poll()

    if (submission == null) {
        println "No submission. Sleeping for 10 seconds."
        sleep(10000) // Sleep 10 seconds if nothing
    } else {
        try {
            def grade = grader.grade(submission)
            println grader.post(grade)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}