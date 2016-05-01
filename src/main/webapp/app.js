angular.module('searchApp', []);

angular.module('searchApp').controller('SearchController',
		[ '$scope', '$http', function($scope, $http) {

			$scope.filter = {};
			
			$scope.results = [];

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
				}).then(function success(response){
					console.log("Success ", response);
					$scope.results = response.data;
				}, function error(response){
					console.log("Error ", response);
				})
			};
			
			$scope.getReviewURL = function(review){
				//console.log(review);
//				var title = review.productURL.split("/")[1];
				return "http://www.amazon.com/gp/aw/review/"+review.asin+"/"+review.reviewId;
			};
		} ]);