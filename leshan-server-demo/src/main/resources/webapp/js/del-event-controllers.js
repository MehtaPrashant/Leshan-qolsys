

var lwdelClientControllers = angular.module('delEventControllers', []);

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

lwdelClientControllers.controller('DelEventCtrl', [
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
        var url ='api/fetch/' +$routeParams.clientId +'/'+$routeParams.event;
        $http.delete(url,{params: {userId: $routeParams.clientId}}). error(function(data, status, headers, config){
            $scope.error = "Unable get client list: " + status + " " + data;
            console.log($routeParams.clientId);
        }).success(function(data, status, headers, config) {
        	 $location.path('/list/' +$routeParams.clientId);
    
        });
        
}]);



	
	                                                                                        