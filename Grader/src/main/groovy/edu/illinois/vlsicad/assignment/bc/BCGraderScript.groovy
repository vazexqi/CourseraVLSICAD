package edu.illinois.vlsicad.assignment.bc

while (true) {
    def grader = new BCGrader();
    def submission = grader.poll()
    if (submission == null) break
    def grade = grader.grade(submission)
    println grader.post(grade)
}