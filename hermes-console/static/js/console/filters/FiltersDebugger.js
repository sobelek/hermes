angular.module('hermes.filters.debugger', ['hermes.filters.repository'])
    .controller('FiltersDebuggerController', ['$scope', '$uibModalInstance', 'FiltersRepository', 'topicName',
        'messageFilters',
        function ($scope, $modal, filtersRepository, topicName, messageFilters) {

            $scope.topicName = topicName;
            $scope.messageFilters = messageFilters;

            resetVerificationState();

            $scope.verify = function () {
                resetVerificationState();
                filtersRepository.verify($scope.topicName, $scope.messageFilters, $scope.message).$promise
                    .then(function (response) {
                        $scope.verificationStatus = response.status;
                        $scope.errorMessage = response.errorMessage;
                    })
                    .catch(function (response) {
                        $scope.verificationStatus = 'ERROR';
                        $scope.errorMessage = response.data.message;
                    })
                    .finally(function () {
                        $scope.verificationInProgress = false;
                    });
            };

            $scope.updateFilters = function () {
                $modal.close({messageFilters: $scope.messageFilters});
            };

            function resetVerificationState() {
                $scope.verificationStatus = '';
                $scope.errorMessage = null;
                $scope.verificationInProgress = false;
            }
        }])
    .factory('FiltersDebuggerModalFactory', ['$uibModal', function ($modal) {
        return {
            open: function (topicName, messageFilters) {
                return $modal.open({
                    templateUrl: 'partials/modal/debugFilters.html',
                    controller: 'FiltersDebuggerController',
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        topicName: function () {
                            return topicName;
                        },
                        messageFilters: function () {
                            return _.cloneDeep(messageFilters);
                        }
                    }
                }).result;
            }
        };
    }]);
