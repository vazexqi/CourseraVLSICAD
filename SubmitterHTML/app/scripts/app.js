'use strict';

var SubmitterHTMLApp = angular.module('SubmitterHTMLApp', ['CourseraGraderUtils']);

SubmitterHTMLApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/main.html',
            controller: 'MainCtrl'
        })
        .otherwise({
            redirectTo: '/'
        });
}]);
