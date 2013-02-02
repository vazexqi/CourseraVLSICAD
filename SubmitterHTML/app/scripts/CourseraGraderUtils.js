'use strict';

var CourseraGraderUtils = angular.module('CourseraGraderUtils', ['ngResource']);

CourseraGraderUtils.factory('Student', function(){
    var Student = function(email, password) {
        this.email = email;
        this.password = password;
    };

    return Student;
});

CourseraGraderUtils.factory('Answer', function() {
    var Answer = function(answer, additionalData) {
        this.answer = typeof answer !== 'undefined' ? answer : '';
        this.additionalData = typeof additionalData !== 'undefined' ? additionalData : '';

        this.answerBase64 = '';
        this.additionalDataBase64 = '';
    };

    Answer.prototype.encode = function() {
        this.answerBase64 = btoa(this.answer);
        this.additionalDataBase64 = btoa(this.additionalData);
    };

    return Answer;
});

CourseraGraderUtils.factory('Submission', function($http) {
    var Submission = function(student, assignmentPart, challenge, challengeAuxillary, challengeResponse, state) {
        this.student = student;
        this.assignmentPart = assignmentPart;
        this.challenge = challenge;
        this.challengeAuxillary = challengeAuxillary;
        this.challengeResponse = challengeResponse;
        this.state = state;
    };

    Submission.prototype.submit = function() {
        var response;

        prepareForSubmission();
        response = submitThroughHTTP();
        return response // TODO: What to return here?
    };

    Submission.prototype.submitThroughHTTP = function() {
    };

    Submission.prototype.prepareForSubmission = function() {
        this.challengeResponse = respondToChallenge();
        this.answer.encode();
    };

    Submission.prototype.respondToChallenge = function() {
        var hash = CryptoJS.SHA1(this.challenge + this.student.password);
        return hash.toString(CryptoJS.enc.Hex);
    };

    return Submission;

});

// NullSubmission is actually a subtype of Submission that does nothing useful
CourseraGraderUtils.factory('NullSubmission', function() {
    var NullSubmission = function(status) {
        this.status = status;
    };

    NullSubmission.prototype.submit = function() {
        return this.status;
    };

    return NullSubmission;

});

CourseraGraderUtils.factory('CourseraHTTPUtils', function($http, NullSubmission, Submission) {
    var utils = {};

    // Static variables
    utils.challengeURL = 'http://localhost:8000/coursera/vlsicad-001/assignment/challenge?';
    utils.submitURL = 'http://localhost:8000/coursera/vlsicad-001/assignment/submit';

    utils.NullSubmission = NullSubmission;
    utils.Submission = Submission;

    utils.initiateSubmission = function(student, assignmentPart) {
        var values, valuesEncoded;
        values = {'email_address': student.email, 'assignment_part_sid':assignmentPart, 'response_encoding': 'delim'};
        valuesEncoded = jQuery.param(values);
        $http.get(utils.challengeURL + valuesEncoded).
            success(function(data, status, headers, config) {
                var splits, submission;

                splits = data.split('|');
                if(splits.length != 9) {
                    submission = new NullSubmission('Badly formatted challenge response' + data);
                } else {
                    submission = new Submission(student, assignmentPart, splits[4], splits[6], splits[8]);
                }
                submission.submit();
            }).
            error(function(data, status, headers, config) {
                // This is an error message meaning that we didn't get a HTTP 200 - 300 response
                console.error("Did not get a response back from Coursera. Please check your connection.");
            });
    };

     return utils;
});