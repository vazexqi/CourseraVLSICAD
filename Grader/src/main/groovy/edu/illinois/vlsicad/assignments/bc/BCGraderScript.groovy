package edu.illinois.vlsicad.assignments.bc

import edu.illinois.vlsicad.core.Grader

while (true) {
    def grader = new BCGrader();
    def submission = grader.poll()
    if (submission == null) break
    def grade = grader.grade(submission)
    println grader.post(grade)
}