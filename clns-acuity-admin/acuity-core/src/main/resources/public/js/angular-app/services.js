'use strict';

/* Services */

angular.module('App.services', ['ngResource'])
    .factory('userInfoFactory', ['$http', function ($http) {
        return {
            getUserInfo: function (cb) {
                return $http.post('/admin/user-info')
                    .success(function (data) {
                        if (cb) {
                            cb(angular.fromJson(data))
                        }

                    })
            }
        }
    }])
    .factory('auditFactory', ['$http', function ($http) {
        return {
            getHistory: function (pageNum, pageSize, sortBy, sortReverse, cb) {
                return $http.post('/api/audit/history', {
                    pageNum: pageNum, pageSize: pageSize,
                    sortBy: sortBy, sortReverse: sortReverse
                })
                    .success(function (data) {
//                        var offset = new Date().getTimezoneOffset();
//                        _.forEach(data.items, function (item) {
//                            item.timestamp -= offset*60 * 1000;
//                        });

                        if (cb) {
                            cb(data)
                        }
                    })
            }
        }
    }])
    .factory('fileViewFactory', ['$http', function ($http) {
        return {
            loadFile: function (fileUrl) {
                return $http.post('/fileView/load', fileUrl);
            },
            listData: function (fileUrl, offset, limit, sortColumns) {
                return $http.post('/fileView/data', {
                    fileUrl: fileUrl,
                    offset: offset,
                    limit: limit,
                    sortColumns: sortColumns
                });
            }
        }
    }])
    .factory('reportService', ['$http', '$log', '$filter', function ($http, $log, $filter) {

        var reportTableSortOptions = [
            {},
            {sortField: 'acuityEntities', reverse: false},
            {sortField: 'dataField', reverse: false},
            {sortField: 'dataField', reverse: false}
        ];
        var model = {
            studyId: null,
            selectedUpload: null,
            dateMask: 'dd-MMM-yyyy',
            timeMask: 'HH:mm',
            reportType: 0,

            reportButtons: [
                'Exception report',
                'Source data table report',
                'Source data field report',
                'Source data value report'
            ],
            reportTypes: ['exception', 'table', 'field', 'value']
        };
        /**
         * Split data array to paged array
         * @param {Array} inList  - input array
         * @param {number} pageSize - count items on the page
         * @return {Array} - paged array
         * //TODO replace with lodash _.chunk
         */
        var groupToPages = function (inList, pageSize) {
            var resultList = [];

            for (var i = 0; i < inList.length; i++) {
                if (i % pageSize === 0) {
                    resultList[Math.floor(i / pageSize)] = [inList[i]];
                } else {
                    resultList[Math.floor(i / pageSize)].push(inList[i]);
                }
            }

            return resultList;
        };
        return {
            model: model,
            groupToPages: groupToPages,
            /**
             * Provides information about loaded reports for the current study
             * @returns {*} - $http promise
             */
            getStudyUploads: function () {
                model.uploadTable.loading = true;
                return $http.get('/uploadreport/' + model.studyId + '/summary')
                    .success(function (data) {
                        model.studyUploads = data;
                        model.selectedUpload = null;
                        model.sortedStudyUploads = groupToPages(data, model.uploadTable.pagingOptions.pageSize);
                        model.studyCode = data[0].studyCode;
                        model.uploadTable.loading = false;
//                        return data;
                    }).error(function (data, status) {
                        console.error('Error getting uploads information for clinical study', status);
                    });
            },

            /**
             * Provides information about exception reports for the current study
             * @returns {*} - $http promise
             */
            getReportOfType: function () {
                model.reportTable.loading = true;
                return $http.get('/uploadreport/' + model.studyId + '/' + model.reportTypes[model.reportType] + '/' + model.selectedUpload.jobExecID)
                    .success(function (data) {
                        model.reportsData[model.reportType] = data;
                        if (model.reportTable.sortOptions[model.reportType].sortField) {
                            data = $filter('orderBy')(model.reportsData[model.reportType], model.reportTable.sortOptions[model.reportType].sortField, model.reportTable.sortOptions[model.reportType].reverse);
                        }
                        model.sortedReports[model.reportType] = groupToPages(data, model.reportTable.pageSize);
                        model.reportTable.pagingOptions[model.reportType].currentPage = 1;
                        model.reportTable.loading = false;
                    }).error(function (data, status) {
                        model.reportTable.loading = false;
                        console.error('Error getting ' + model.reportTypes[model.reportType] + ' reports for clinical study', status);
                    });
            },
            resetDataOptions: function () {
                model.reportsData = [];
                model.sortedReports = [];
                model.reportTable.sortOptions = _.cloneDeep(reportTableSortOptions);
                _.each(model.reportTable.pagingOptions, function (option) {
                    option.currentPage = 1;
                });
            }
        };
    }])
    .factory('uploadSummaryService', ['$http', function ($http) {
        var model = {
            dateMask: 'dd-MMM-yyyy',
            uploadSummary: [],
            totalSummary: {filesCount: 0, filesSize: 0},
            averageSummary: {filesCount: 0, filesSize: 0}
        };
        var months = ["Jan", "Feb", "Mar",
            "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"];

        function formatDate (date) {
            var d = new Date(date);
            var day = d.getDate();
            var month = months[d.getMonth()];
            var year = d.getFullYear() % 100;
            return day + '-' + month + '-' + year;
        }

        function sumBy (array, property) {
            return array.map(function (d) {
                return d[property]
            }).reduce(function (previousValue, currentValue) {
                return previousValue + currentValue;
            })
        }

        function round (value) {
            return Math.round(value * 100) / 100;
        }

        return {
            model: model,
            /**
             * Provides summary information about upload reports for all     studies
             * @returns {*} - $http promise
             */
            getUploadSummary: function (dateFrom, dateTo) {
                model.loading = true;
                return $http.post('/uploadreport/upload-summary',
                    {
                        dateFrom: dateFrom ? formatDate(dateFrom) : undefined,
                        dateTo: dateTo ? formatDate(dateTo) : undefined
                    })
                    .success(function (data) {
                        model.uploadSummary = data.sort(function (a, b) {
                            return a.date - b.date;
                        });
                        var nonEmptyUploadDates = data.filter(function (d) {
                            return !!d.filesSize;
                        }).length;
                        var totalFilesCount = data && data.length > 0 && nonEmptyUploadDates > 0
                            ? sumBy(data, 'filesCount')
                            : 0;
                        var totalFilesSize = data && data.length > 0 && nonEmptyUploadDates > 0
                            ? sumBy(data, 'filesSize')
                            : 0;

                        model.totalSummary = {
                            filesCount: totalFilesCount,
                            filesSize: totalFilesSize
                        };

                        // Please note that average summary is calculated taking into account
                        // only those days when any file was uploaded and upload was non-empty
                        model.averageSummary = {
                            filesCount: totalFilesCount ? round(totalFilesCount / nonEmptyUploadDates) : 0,
                            filesSize: totalFilesSize ? round(totalFilesSize / nonEmptyUploadDates) : 0
                        };
                        model.loading = false;
                    }).error(function (data, status) {
                        model.loading = false;
                        console.error('Error getting upload summary information for clinical studies', status);
                    });
            }
        };
    }])

