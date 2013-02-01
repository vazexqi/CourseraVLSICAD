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

CourseraGraderUtils.factory('Submission', function($resource) {
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
        this.challengeResponse = responseToChallenge();
        this.answer.encode();
    };

    Submission.prototype.respondToChallenge = function() {
        var hash = CryptoJS.SHA1(this.challenge + this.student.password);
        return hash.toString(CryptoJS.enc.Hex);
    };

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

CourseraGraderUtils.factory('CourseraHTTPUtils', function($resource) {
    var utils = {};

    // Static variables
    utils.challengeURL = 'https://class.coursera.org/vlsicad-001/assignment/challenge';
    utils.submitURL = 'https://class.coursera.org/vlsicad-001/assignment/submit';

    utils.getChallenge = function(student, assignmentPart) {
        var http, values, response, text;


    };

     return utils;
});