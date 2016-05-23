	var app = angular.module("testcase-app", [ 'ngSanitize' ]);
	app.controller("testcase-controller", function($scope, $http) {

		console.log("started app");
		$scope.results = [];
		$scope.components = [];

		$scope.removeComponents = function(x) {
			$scope.components.splice(x, 1);
		}
		
		$scope.removeComponentsAttr = function(compIndex,attrName,key) {
			delete $scope.components[compIndex][attrName][key];
		}
		
		$scope.addComponentsAttr = function(compIndex,attrName) {
			var len = Object.keys($scope.components[compIndex][attrName]).length
			$scope.components[compIndex][attrName]['key'+len]='value'+len;
		}
		$scope.addComponentBody= function(compIndex){
			$scope.components[compIndex]['reqBody']="{}";
		}
		$scope.addBasicAuth=function(compIndex){
			$scope.components[compIndex]['isBasicAuth']=true;
		}
		$scope.removeBasicAuth=function(compIndex){
			$scope.components[compIndex]['isBasicAuth']=false;
		}
		$scope.addDependsUpon=function(){
			$('#showDependsUponDialog').modal('show');
		}
		
		$scope.hideComponents = function(index) {
			console.log("hiding....");
			$scope.components[index].show=!$scope.components[index].show;
			//hideComponents.show = !hideComponents.show; 
		}
		
		$scope.addComponent = function(reqId) {
			console.log(">>>>>" + reqId)
			$http.get("/basic-request/" + reqId).then(function(response) {
				console.log(response.data);
				response.data.show =false;
				response.data.index=1+response.data.index;
				
				console.log(response.data.dependsUpon);
				
				var depends=response.data.dependsUpon;
				
				for (var key in depends){
					alert('Adding dependent component: '+depends[key])
					$scope.addComponent(key);
				}
				
				$scope.components.push(response.data);

			}, function(response) {
				//Second function handles error
				$scope.content = "Something went wrong";
			});

		}
		
		$scope.addComponentType = function(reqType) {
			console.log(">>>>>" + reqType)
			$http.get("/basic-request/" + reqType).then(function(response) {
				console.log(response.data);
				response.data.show =false;
				response.data.index=1+response.data.index;
				response.data.responseType='JSON';
//				response.data.dependsUpon = {};

				$scope.components.splice(0,1,response.data);

			}, function(response) {
				//Second function handles error
				$scope.content = "Something went wrong";
			});
		}
		
		$scope.addDepends = function(compIndex,reqId,reqName) {
			console.log(">>>>>" + compIndex);
			$scope.components[compIndex].dependsUpon[reqId]=reqName;
			console.log($scope.components[compIndex].dependsUpon);
			$('#showDependsUponDialog').modal('hide');

		}
		
		$scope.removeDependsUpon = function(compIndex,reqId) {
			console.log(">>>>>" + reqId);
			delete $scope.components[compIndex].dependsUpon[reqId];
			console.log($scope.components[compIndex].dependsUpon);
			$('#showDependsUponDialog').modal('hide');

		}
		
		$scope.saveBasicRequest= function(reqId){
			$('#myPleaseWait').modal('show');
			console.log($scope.components);
			$http.post('/save-unit-request', JSON.stringify($scope.components)).
		    success(function(data, status, headers, config) {
		      console.log(data);
		      }).
		      error(function(data, status, headers, config) {
		        alert('not able to save!!!');
		      }).then(function() {
			      $('#myPleaseWait').modal('hide');
				});
		}
		$scope.saveTestCase= function(testCaseId){
			$('#myPleaseWait').modal('show');
			console.log($scope.components);
			$http.post('/testcase/save/'+testCaseId, JSON.stringify($scope.components)).
		    success(function(data, status, headers, config) {
		      console.log(data);
		      }).
		      error(function(data, status, headers, config) {
		        alert('not able to save!!!');
		      }).then(function() {
			      $('#myPleaseWait').modal('hide');
				});
		}
		$scope.runTestCase= function(testCaseId){
			$('#myPleaseWait').modal('show');
			console.log($scope.components);
			$http.post('/testcase/run/'+testCaseId, JSON.stringify($scope.components)).
		    success(function(data, status, headers, config) {
		      console.log(data);
		      }).
		      error(function(data, status, headers, config) {
		        alert('not able to save!!!');
		      }).then(function() {
			      $('#myPleaseWait').modal('hide');
				});
		}
		
		$scope.getResults = function() {
			console.log("searching...");
			$http.post("/search-result",
					JSON.stringify($("#searchForm").serializeArray())).success(
					function(data, status) {
						$scope.results = data;
						console.log(data.length);
						$('[data-toggle="tooltip"]').tooltip();
					}).then(function() {
				$('[data-toggle="tooltip"]').tooltip();
				console.log("tooltip...");
			});
		}
		
		$scope.showOnConsole = function(testCaseId) {
			console.log("loading test case: "+testCaseId);
			
		}
		
		$scope.start=function(){
			console.log('init');
		}
		
	});
