/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Achim Kraus (Bosch Software Innovations GmbH) - fix typo in notificationCallback
 *                                                     processing multiple resources
 *******************************************************************************/

var lwListDetailsControllers = angular.module('listDetailsControllers', []);

// Update client in a list of clients (replaces the client with the same endpoint))
function updateClient(updated, clients) {
    return clients.reduce(function(accu, client) {
        if (updated.endpoint === client.endpoint) {
            accu.push(updated);
        } else {
            accu.push(client);
        }
        return accu;
    }, []);
}

lwListDetailsControllers.controller('ListDetailsCtrl', [
    '$scope',
    '$http',
    '$location',
    '$routeParams',
    'lwResources',
    '$filter',
    function ListDetailsCtrl($scope, $location, $routeParams, $http, lwResources,$filter) {
        // add function to show client
        $scope.showClient = function(client) {
            $location.path('/clients/' + client.endpoint);
        };
        
        $scope.deleteEvent = function(event,clientEP) {
           console.log(event);
           console.log(clientEP);
            var url ='api/fetch/' +$routeParams.clientId +'/'+event;
            
        };
        
        $scope.clientId = $routeParams.clientId;
        // get the list of connected clients
        $http.get('api/fetch' + $routeParams.clientId). error(function(data, status, headers, config){
            $scope.error = "Unable get client list: " + status + " " + data;
            console.error($scope.error);
        }).success(function(data, status, headers, config) {
            $scope.clients = data;

            // HACK : we can not use ng-if="clients"
            // because of https://github.com/angular/angular.js/issues/3969
            $scope.clientslist = true;

            // listen for clients registration/deregistration
            $scope.eventsource = new EventSource('event');

            var registerCallback = function(msg) {
                $scope.$apply(function() {
                    var client = JSON.parse(msg.data);
                    $scope.clients.push(client);
                });
            };

            var updateCallback =  function(msg) {
                $scope.$apply(function() {
                    var client = JSON.parse(msg.data);
                    $scope.clients = updateClient(client, $scope.clients);
                });
            };
            
            $scope.eventsource.addEventListener('REGISTRATION', registerCallback, false);

            $scope.eventsource.addEventListener('UPDATED', updateCallback, false);
            
            var getClientIdx = function(client) {
                for (var i = 0; i < $scope.clients.length; i++) {
                    if ($scope.clients[i].registrationId == client.registrationId) {
                        return i;
                    }
                }
                return -1;
            };
            var deregisterCallback = function(msg) {
                $scope.$apply(function() {
                    var clientIdx = getClientIdx(JSON.parse(msg.data));
                    if(clientIdx >= 0) {
                        $scope.clients.splice(clientIdx, 1);
                    }
                });
            };
            $scope.eventsource.addEventListener('DEREGISTRATION', deregisterCallback, false);
        });
}]);

