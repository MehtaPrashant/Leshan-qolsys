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

var lwdelClientControllers = angular.module('delClientControllers', []);

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

lwdelClientControllers.controller('DelClientCtrl', [
    '$scope',
    '$http',
    '$location',
    '$routeParams',
    function ClientListCtrl($scope, $http,$location,$routeParams) {

        // update navbar
        angular.element("#navbar").children().removeClass('active');
        angular.element("#client-navlink").addClass('active');

        // free resource when controller is destroyed
        $scope.$on('$destroy', function(){
            if ($scope.eventsource){
                $scope.eventsource.close()
            }
        });
        // get the list of connected clients
        var url ='api/fetch/' +$routeParams.clientId;
        $http.delete(url,{params: {userId: $routeParams.clientId}}). error(function(data, status, headers, config){
            $scope.error = "Unable get client list: " + status + " " + data;
            console.log($routeParams.clientId);
        }).success(function(data, status, headers, config) {
        	 $location.path('/list/');
    
        });
        
}]);



	
	                                                                                        