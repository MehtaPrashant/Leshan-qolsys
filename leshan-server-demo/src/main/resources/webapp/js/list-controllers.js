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

var lwListControllers = angular.module('listControllers', []);

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

lwListControllers.controller('ListCtrl', [
    '$scope',
    '$http',
    '$location',
    function ClientListCtrl($scope, $http,$location) {

        // update navbar
        angular.element("#navbar").children().removeClass('active');
        angular.element("#client-navlink").addClass('active');

        // free resource when controller is destroyed
        $scope.$on('$destroy', function(){
            if ($scope.eventsource){
                $scope.eventsource.close()
            }
        });
        
        $scope.showModal = function() {
            $('#newSecurityModal').modal('show');
            $scope.$broadcast('show-errors-reset');
            $scope.endpoint = ''
            $scope.securityMode = 'psk'
            $scope.pskIdentity = ''
            $scope.pskValue = ''
            $scope.rpkXValue = ''
            $scope.rpkYValue = ''
            $scope.defaultParams = 'secp256r1'
       }
        
        
        
        $scope.save = function() {
            $scope.$broadcast('show-errors-check-validity');
            if ($scope.form.$valid) {
                if($scope.securityMode == "psk") {
                    var security = {endpoint: $scope.endpoint, psk : { identity : $scope.pskIdentity , key : $scope.pskValue}};
                }
                else {
                    var security = {endpoint: $scope.endpoint, rpk : { x : $scope.rpkXValue , y : $scope.rpkYValue, params : $scope.rpkParamsValue || $scope.defaultParams}};
                }
                if(security) {
                    $http({method: 'PUT', url: "api/security/clients/", data: security, headers:{'Content-Type': 'text/plain'}})
                    .success(function(data, status, headers, config) {
                        $scope.securityInfos[$scope.endpoint] = security;
                        $('#newSecurityModal').modal('hide');
                    }).error(function(data, status, headers, config) {
                        errormessage = "Unable to add security info for endpoint " + $scope.endpoint + ": " + status + " - " + data;
                        dialog.open(errormessage);
                        console.error(errormessage)
                    });
                }
            }
        }

        // add function to show client
        $scope.showClient = function(client) {
            $location.path('/clients/' + client.endpoint);
        };
        
        
        

        // the tooltip message to display for a client (all standard attributes, plus additional ones)
        $scope.clientTooltip = function(client) {
            var standard = ["Lifetime: " + client.lifetime + "s",
                            "Binding mode: " + client.bindingMode,
                            "Protocol version: " + client.lwM2mVersion,
                            "Address: " + client.address];

            var tooltip = standard.join("<br/>");
            if (client.additionalRegistrationAttributes) {
                var attributes = client.additionalRegistrationAttributes;
                var additionals = [];
                for (key in attributes) {
                    var value = attributes[key];
                    additionals.push(key + " : " + value);
                }
                if (additionals.length>0){
                    tooltip = tooltip + "<hr/>" + additionals.join("<br/>");
                }
            }
            return tooltip;
        };
        
        

        // get the list of connected clients
        $http.get('api/fetch'). error(function(data, status, headers, config){
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


lwListControllers.controller('ListdetailsCtrl', [
    '$scope',
    '$location',
    '$routeParams',
    '$http',
    'lwResources',
    '$filter',
	function ClientListCtrl($scope, $location, $routeParams, $http, lwResources,$filter) {
	    // get the list of connected clients
	    $scope.clientId = $routeParams.clientId;
	  
	    $http.get('api/fetch/'+$scope.clientId). error(function(data, status, headers, config){
	        $scope.error = "Unable get client list: " + status + " " + data;
	        
	        console.error($scope.error);
	    }).success(function(data, status, headers, config) {
	        $scope.clients = data;
	        console.log(data);
	
	        // HACK : we can not use ng-if="clients"
	        // because of https://github.com/angular/angular.js/issues/3969
	        $scope.clientslist = true;
	    });
	}]);
	
	                                                                                        