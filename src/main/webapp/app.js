angular.module('searchApp', []);

angular.module('searchApp').controller('SearchController',
		[ '$scope', '$http', function($scope, $http) {

			$scope.filter = {};

			var _urls = {
				search : 'search.do'
			}

			$scope.doSearch = function() {
				console.log('Doing search');
				$http.get(_urls.search, {
					params : {
						product : $scope.filter.product,
						feature : $scope.filter.feature
					}
				});
			}
		} ]);