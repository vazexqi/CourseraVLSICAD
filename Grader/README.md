Intro
-----

Please familiarize yourself with the introductory material for external graders on the Coursera website http://support.coursera.org/customer/portal/articles/573466-server-side-execution-external-custom-graders before attempting to run the graders. In particular, you would need to be familiar with how to set up a new queue to receive submissions.

You should also set up a toy assignment that can receive submissions.

Running
--------

`./gradlew bcGrader`

This is a very simple example that will fork off a process that reads a submission, runs the commands (bc commmand line calculator commands) embedded in its payload and report back the standard out and standard err of the process.

`./gradlew kbddGrader`

This is an example that will actually fork off the `script` command line tool to run kbdd. We do this so that we can capture the entire output from kbdd (including all the echoes). This will actually run in a while loop and post back results to Coursera. Be sure to change the API key in the config.groovy file  (we have added a placeholder there to prevent sharing that key).

