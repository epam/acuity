/*-
 * #%L
 * eTarget Maven Webapp
 * %%
 * Copyright (C) 2017 - 2021 digital ECMT
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
var app = angular.module('targetapp', ['ui.bootstrap']);
app.filter('unique', function() {
   return function(collection, keyname, keyname2) {
      var output = [], 
          keys = [];
      angular.forEach(collection, function(item) {
    	  if(item==null) return;
          var key = item[keyname]+item[keyname2];
          if(keys.indexOf(key) === -1) {
              keys.push(key); 
              output.push(item);
          }
      });
      return output;
   };
});

app.filter('unique3', function() {
	   return function(collection, keyname, keyname2, keyname3) {
	      var output = [], 
	          keys = [];
	      angular.forEach(collection, function(item) {
	          var key = item[keyname]+item[keyname2]+item[keyname3];
	          if(keys.indexOf(key) === -1) {
	              keys.push(key); 
	              output.push(item);
	          }
	      });
	      return output;
	   };
	});

app.filter('unique4', function() {
	   return function(collection, keyname, keyname2, keyname3, keyname4) {
	      var output = [], 
	          keys = [];
	      angular.forEach(collection, function(item) {
	          var key = item[keyname]+item[keyname2]+item[keyname3]+item[keyname4];
	          if(keys.indexOf(key) === -1) {
	              keys.push(key); 
	              output.push(item);
	          }
	      });
	      return output;
	   };
	});


app.filter('unique_list', function() {
   return function(collection) {
      var keys = [];
      
      angular.forEach(collection, function(item) {
          if(keys.indexOf(item) === -1) {
              keys.push(item); 
          }
      });
      return keys;
   };
});

app.filter('searchDisease', function () {
	return function(allPatients, searchterm, disease, unknownsig) {
		var output =[];
		var exact=false;
	    if(typeof disease != "undefined" && disease === searchterm) {
	    	exact=true;
	    }
		if(!searchterm){
			return allPatients;
		}
		angular.forEach(allPatients, function(patient) {
		  if(exact==true){
		    if((patient.conditionName.toLowerCase() === searchterm.toLowerCase() ||
		    		(patient.conditionSubtype!=null && patient.conditionSubtype.toLowerCase() === searchterm.toLowerCase()))
		    		&& (unknownsig==true || patient.unknownSignificant=='false')){
	            output.push(patient);
	        }
		  } else {
			if((patient.conditionName.toLowerCase().indexOf(searchterm.toLowerCase()) !== -1 ||
					(patient.conditionSubtype!=null && patient.conditionSubtype.toLowerCase().indexOf(searchterm.toLowerCase())) !== -1)
					&& (unknownsig==true || patient.unknownSignificant=='false')){
	            output.push(patient);
	        }
	      }
	
		});
		return output;
	};
});

app.filter('searchGene', function () {
	return function(allPatients, searchterm, disease, unknownsig) {
		var output =[];
		var exact=false;
	    if(typeof disease != "undefined" && disease === searchterm) {
	    	exact=true;
	    }
	
		if(!searchterm || searchterm==''){
			return allPatients;
		}
		
		var parts=searchterm.split(" ");
		parts.push(searchterm);
		
		angular.forEach(parts, function(part){
			angular.forEach(allPatients, function(patient) {
				if(unknownsig==true || patient.unknownSignificant=='false') {
				  if(exact==true){
				    if(patient.geneName !=null && patient.geneName.toLowerCase() === part.toLowerCase()){
			            output.push(patient);
			        }
				    if(patient.rearr_gene1 !=null && patient.rearr_gene1.toLowerCase() === part.toLowerCase()){
			            output.push(patient);
			        }
				    if(patient.rearr_gene2 !=null && patient.rearr_gene2.toLowerCase() === part.toLowerCase()){
			            output.push(patient);
			        }
				  } else {
					if(patient.geneName !=null && patient.geneName.toLowerCase().indexOf(part.toLowerCase()) !== -1){
			            output.push(patient);
			        }
					if(patient.rearr_gene1 !=null && patient.rearr_gene1.toLowerCase().indexOf(searchterm.toLowerCase()) !== -1){
			            output.push(patient);
			        }
					if(patient.rearr_gene2 !=null && patient.rearr_gene2.toLowerCase().indexOf(part.toLowerCase()) !== -1){
			            output.push(patient);
			        }
			      }
				}
			
			});
		});
		return output;
	};
});

app.filter('unknownSig', function() {
	return function(allGenes, unknownsig) {
		if(unknownsig==true){
			return allGenes
		}
		var output =[];
		angular.forEach(allGenes, function(gene) {
			if(gene.unknown_significant==0) {
				output.push(gene);
			}
		});
		return output;
	}
});

app.filter('objLength', function() { 
 return function(object) { 
	 if(object==null || typeof object=="undefined")
		 return 0;
  return Object.keys(object).length; 
 } 
});

app.filter('filterterm', function() {
	   return function(collection, term, value) {
	      var keys = [];
	      angular.forEach(collection, function(item) {
	          if(item[term] === value) {
	              keys.push(item); 
	          }
	      });
	      return keys;
	   }
});

app.controller("targetController", function($scope, $sce, $http, $timeout, $window, $anchorScroll, $filter) {
  window.MY_SCOPE = $scope;
  // Page vars
  $scope.page = {
    list: true,
    detail: false,
    status: false,
    genepanel: false,
    admin: false,
    search: false
  };

  $scope.detailSection = {
	addReports: false,
    tumourNGS: false,
    tumourSubs: {
      1: true,
      2: false,
      3: false,
      4: false,
      5: false,
      6: false
      
    },
    foundationTumour: false,
    fmTumourSubs: {
        1: true,
        2: false,
        3: false,
        4: false,
        5: false,
        6: false 
    },
    ihc: false,
    ihcSubs: {
        1: true,
        2: false,
        3: false,
        4: false,
        5: false,
        6: false,
        7: false,
        8: false,
        9: false,
        10: false,
        11: false,
        12: false,
        13: false,
        14: false,
        15: false,
        16: false,
        17: false,
        18: false,
        19: false
    },
    qciSubs: {
    	T1: true,
    	T2: false,
    	T3: false,
    	T4: false,
    	T5: false,
    	T6: false,
    	T7: false,
    	T8: false,
    	T9: false,
    	T10: false,
    	T11: false,
    	T12: false,
    	T13: false,
    	T14: false,
    	T15: false,
    	T16: false,
    	T17: false,
    	T18: false,
    	T19: false,
    	T20: false,
    	T21: false
    },
    ctdnaNGS: false,
    ctdnaNGSSubs: {
        1: true,
        2: false,
        3: false,
        4: false,
        5: false,
        6: false,
        7: false,
        8: false,
        9: false,
        10: false,
        11: false,
        12: false,
        13: false,
        14: false,
        15: false,
        16: false,
        17: false,
        18: false,
        19: false,
        20: false,
        21: false,
        22: false,
        23: false
    },
    ctdnaExploratory: false,
    fmBloodSubs: {
      1: true,
      2: false,
      3: false,
      4: false,
      5: false,
      6: false,
      7: false,
      8: false,
      9: false,
      10: false,
      11: false,
      12: false,
      13: false,
      14: false,
      15: false,
      16: false,
      17: false,
      18: false,
      19: false,
      20: false,
      21: false,
      22: false,
      23: false
      },
    foundationBlood: false,
    general: true,
    showhide: {
      toggle: true,
      toggleExp: true
    },
    ngsSubset: {
      ngsLib: {},
      noResults: {
        message: '',
        specimenDate: ''
      }
    },
    exploratory: {
      noResults: {
        message: '',
        specimenDate: ''
      }
    }
  };

  $scope.currentPatient = {
    id: '',
    person_id: '',
    disease: '',
    age: '',
    gender: '',
    site:'',
    dateDiagnosis: '',
    dateConsent: '',
    treatments: {},
    samples: {
      1: {dateBlood: '', dateTumour: '', tumourType: '', tumourNature: '', pdxCDX: '', dateGDL: ''}
    },
    tumourNGS: {
      baseline: {}
    },
    reportGDL: {},
    reportCEP: {},
    ngsSubset: {
      listGenes: {},
      baseline: {},
      ngsLib: {}
    },
    exploratory: {
      baseline: {},
      ngsLib: {}
    },
    fmBlood: {
    	baseline: {}
    },
    significantMutations: {
      tumourNGS: [],
      ctDNA: [],
      fmTumour: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      fmBlood: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      latestTumorNGS:[],
      latestCtDNA: [],
      meetingOutcomeSummaryCNA: {},
      meetingOutcomeSummaryR: {},
      meetingOutcomeSummarySV: {},
      latestFmTumour: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      latestFmBlood: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      summery:[],
      ihc: {}
    }
  };

  $scope.active_ctdna = {};
  $scope.active_exploratory = {};
  $scope.active_tumour = {};
  $scope.active_FMTumour = {1:{}};
  $scope.active_FMBlood = {1:{}}
  $scope.active_ihc = {1:{}};
  
  $scope.currentUser = 'user';
  $scope.trustAsHtml = $sce.trustAsHtml;
  $scope.pdxcdx = {};
  $scope.system = {
    status: {}
  };
  $scope.meeting = {
    edits: 0,
    outcome: {},
    new: false,
    add: {
      date: '',
      outcome: '',
      notes: ''
    },
    message: '',
    flag: false
  };
  $scope.print = {
    index: 0,
    targetID: '',
    mtbDate: '',
    outcome: '',
    notes: '',
    printedBy: '',
    datePrinted: '',
    lastUpdated: ''
  };

  $scope.showBloodVUS=false
  $scope.showTumourVUS=false

  $scope.mutationPollRunning = 0;
  
  $scope.search = {
	s: {},
  	disease: '',
  	gene: '',
  	diseaseList: [],
  	geneList: {},
  	pdxcdxList: [],
  	tabNum: 1,
  	unknownSig: false
  };

  $scope.admin = {
    users: {},
    newUser: {
      email: '',
      roleID: 3
    },
    editUser: []
  };
  
  $scope.patientView = {
	orderByField: 'none',
	order: 0
  };
  
  $scope.MOView = {
		orderByField: ['source','gene'],
		order: 0
	  };
  
  $scope.genePanel = {
	name: '',
	geneList: [],
	geneModel: '',
	message: ''
  };
  
  $scope.timestamp = Date.now();
  $scope.allGenesPanel = {};
  
  generic_li=angular.element('.generic-genomics-data');
  $scope.currentPatient.significantMutations.genericGenomic={}
  $scope.currentPatient.significantMutations.latestGenericGenomic={}
  for(i=0;i<generic_li.length;i++){
	  name=generic_li[i].id;
	  if(name.startsWith("detail-sidebar-")){
		  source=name.slice(15);
	  }
	  
	  $scope.detailSection[source]=false;
	  $scope.detailSection[source+'Subs']={};
	  $scope.detailSection[source+'Subs'][1]=true;
	  for(j=2;j<24;j++){
		  $scope.detailSection[source+'Subs'][j]=false;
	  }
	  $scope.currentPatient.significantMutations.genericGenomic[source]= {
	    	  copyNumberAlteration: [],
	    	  rearrangement: [],
	    	  shortVariant: []
	  }
	  $scope.currentPatient.significantMutations.latestGenericGenomic[source]= {
 	    	  copyNumberAlteration: [],
 	    	  rearrangement: [],
 	    	  shortVariant: []
 	  }
  }

  $scope.$watch('active_exploratory', function(newValue, oldValue){
     Object.keys($scope.currentPatient.exploratory.baseline).forEach(function(baseline, i) {
		Object.keys($scope.currentPatient.exploratory.baseline[baseline].runs).forEach(function(key,index) {
		    // key: the name of the object key
		    // index: the ordinal position of the key within the object 
		    if(newValue[baseline][key]==null){
		    	$scope['active_exploratory'][baseline][key]=''+Object.keys($scope.currentPatient.exploratory.baseline[baseline].runs[key]).length;
		    }
		});
	});
  }, true);
  
  $scope.$watch('active_ctdna', function(newValue, oldValue){
     Object.keys($scope.currentPatient.ngsSubset.baseline).forEach(function(baseline, i) {
		Object.keys($scope.currentPatient.ngsSubset.baseline[baseline].runs).forEach(function(key,index) {
		    // key: the name of the object key
		    // index: the ordinal position of the key within the object 
		    if(newValue[baseline][key]==null){
		    	$scope['active_ctdna'][baseline][key]=''+Object.keys($scope.currentPatient.ngsSubset.baseline[baseline].runs[key]).length;
		    }
		});
	});
  }, true);
  
  $scope.$watch('active_tumour', function(newValue, oldValue){
	     Object.keys($scope.currentPatient.tumourNGS.baseline).forEach(function(baseline, i) {
			Object.keys($scope.currentPatient.tumourNGS.baseline[baseline].runs).forEach(function(key,index) {
			    // key: the name of the object key
			    // index: the ordinal position of the key within the object 
			    if(newValue[baseline][key]==null){
			    	$scope['active_tumour'][baseline][key]=''+Object.keys($scope.currentPatient.tumourNGS.baseline[baseline].runs[key]).length;
			    }
			});
		});
	  }, true);
	  
  $scope.$watch('active_FMTumour', function(newValue, oldValue){
	  if(typeof $scope.currentPatient.fmTumour != 'undefined'){
		  $timeout.cancel($scope.fmtimout);
		  $scope.fmtimout=$timeout(function() {
            	if(typeof $scope.active_FMTumour[1][1]!='undefined' && $scope.active_FMTumour[1][1]==null){
	            	Object.keys($scope.currentPatient.fmTumour).forEach(function(baseline, i) {
	            		if($scope.active_FMTumour[1][baseline]==null){
	     				  $scope.active_FMTumour[1][baseline]=''+(Object.keys($scope.currentPatient.fmTumour[baseline]).length-1);
	            		}
	     			});
	            }
			}, 500);
	  }
  }, true); 
  
  $scope.$watch('active_FMBlood', function(newValue, oldValue){
	  if(typeof $scope.currentPatient.fmBlood != 'undefined' && typeof $scope.currentPatient.fmBlood.baseline != 'undefined'){
		  $timeout.cancel($scope.fmbloodtimeout);
		  $scope.fmbloodtimeout=$timeout(function() {
            	if(typeof $scope.active_FMBlood[1][1]!='undefined' && $scope.active_FMBlood[1][1]==null){
	            	Object.keys($scope.currentPatient.fmBlood.baseline).forEach(function(baseline, i) {
	            		if($scope.active_FMBlood[1][baseline]==null){
	     				  $scope.active_FMBlood[1][baseline]=''+(Object.keys($scope.currentPatient.fmBlood.baseline[baseline]).length-1);
	            		}
	     			});
	            }
			}, 500);
	  }
  }, true); 

  $scope.$watch('active_ihc', function(newValue, oldValue){
	  if(typeof $scope.currentPatient.ihc != 'undefined'){
	     Object.keys($scope.currentPatient.ihc).forEach(function(baseline, i) {
			Object.keys($scope.currentPatient.ihc[baseline]).forEach(function(key,index) {
			    // key: the name of the object key
			    // index: the ordinal position of the key within the object 
			    if(newValue[1][baseline]==null){
			    	$scope['active_ihc'][1][baseline]=''+Object.keys($scope.currentPatient.ihc[baseline]).length;
			    }
			});
		});
	  }
  }, true);
 
  $http({
    method: 'GET',
    url: 'api/data.jsp?endpoint=currentuser'
  }).then(function successCallback(response) {
    $scope.currentUser = response.data;
  }, function errorCallback(response) {
    //console.log('Error fetching current user: '+response.status);
  });
  
  $scope.getAllPatients = function() {
    $http({
      method: 'GET',
      url: 'api/data.jsp?endpoint=patients'
    }).then(function successCallback(response) {
      $scope.allPatients = response.data;
      $scope.timestamp = Date.now();
    }, function errorCallback(response) {
      //console.log('Error fetching list of patients: '+response.status);
    });
  };
  
  $scope.getAllPatientsSearch = function() {
    $http({
      method: 'GET',
      url: 'rest/search/patients'
    }).then(function successCallback(response) {

      $scope.allPatientsSearch = response.data;

    }, function errorCallback(response) {
      //console.log('Error fetching list of patients: '+response.status);
    });
  };
  
  $scope.getAllGenesSearch = function() {
    $http({
      method: 'GET',
      url: 'rest/search/genes'
    }).then(function successCallback(response) {
      $scope.searchableGenes = response.data;

    }, function errorCallback(response) {
      //console.log('Error fetching list of patients: '+response.status);
    });
  };

//  $scope.getConditionsSearch = function(condition) {
//    $http({
//      method: 'GET',
//      url: 'api/data.jsp?endpoint=searchCondition&condition='+encodeURIComponent(condition)
//    }).then(function successCallback(response) {
//      $scope.allPatientsSearch = response.data;
//      $scope.onSelectDisease(condition);
//    }, function errorCallback(response) {
//      //console.log('Error fetching list of patients: '+response.status);
//    });
//  };
//  
//  $scope.getGenesSearch = function(gene) {
//    $http({
//      method: 'GET',
//      url: 'api/data.jsp?endpoint=searchGene&gene='+encodeURIComponent(gene)
//    }).then(function successCallback(response) {
//      $scope.allPatientsSearch = response.data;
//      $scope.onSelectGene(gene);
//    }, function errorCallback(response) {
//      //console.log('Error fetching list of patients: '+response.status);
//    });
//  };
  
  $scope.getConditionsSearch = function(condition) {
    $http({
      method: 'GET',
      url: 'rest/search/conditions/'+encodeURIComponent(condition)
    }).then(function successCallback(response) {
      $scope.allPatientsSearch = response.data;
      $scope.onSelectDisease(condition);
    }, function errorCallback(response) {
      //console.log('Error fetching list of patients: '+response.status);
    });
  };
  
  $scope.getGenesSearch = function(gene) {
    $http({
      method: 'GET',
      url: 'rest/search/genes/'+encodeURIComponent(gene)
    }).then(function successCallback(response) {
      $scope.allPatientsSearch = response.data;
      $scope.onSelectGene(gene);
    }, function errorCallback(response) {
      //console.log('Error fetching list of patients: '+response.status);
    });
  };


  $scope.getAllConditionsSearch = function() {
    $http({
      method: 'GET',
      url: 'rest/search/conditions'
    }).then(function successCallback(response) {
      $scope.searchableConditions = response.data;
    }, function errorCallback(response) {
      //console.log('Error fetching list of patients: '+response.status);
    });
  };
  
  $scope.getPDXCDXPatients = function() {
	  $http({
		method: 'GET',
		url: 'rest/search/pdxcdxPatients'
	  }).then(function successCallback(response) {
		  $scope.search.pdxcdxList = response.data;
	    }, function errorCallback(response) {
	      //console.log('Error fetching list of patients: '+response.status);
	    });
	  
  }

  
  // Search 
  // Disease
  $scope.setSearchTab = function(tabNum){
	 $scope.search.unknownSig=false;
     $scope.search.tabNum=tabNum;
  };
  
  $scope.isSearchTabSet = function(tabNum) {
  	return $scope.search.tabNum === tabNum;
  };
  
  $scope.onSelectDiseaseA = function($item, $doit) {
	  if($doit){
		  $scope.onSelectDisease($item)
	  }
	  
  }
  
  $scope.onSelectDisease = function($item) {
	if(typeof $item!='undefined'){ 
		$scope.search.disease = $item;
	} else {
		$scope.search.disease = '';
	}
		
    var output =[];
    
	$scope.search.diseaseList=[];
    
	angular.forEach($scope.allPatientsSearch, function(patient) {
		if(patient.conditionName.toLowerCase() === $scope.search.disease.toLowerCase() || 
				(patient.conditionSubtype!=null && patient.conditionSubtype.toLowerCase() ===$scope.search.disease.toLowerCase())
				&& ($scope.search.unknownSig==true || patient.unknownSignificant=='false')){
            output.push(patient);
        }
	});
	angular.forEach(output, function(ind) {
		var obj = $scope.search.diseaseList.filter(function(a) {
				if(typeof a['rearr_gene1'] != 'undefined'){
					return (a['rearr_gene1']+' '+a['rearr_gene2'])===ind.geneName;
				} else {
					return a['geneName']===ind.geneName;
				}
			})[0]
		if($scope.search.unknownSig==true || ind.unknownSignificant=='false') {
			if(typeof obj != "undefined") {
				var obj2 = obj.patientIDs.filter(function(b) { return b['personID']===ind.personID;})[0]
				if(typeof obj2 == "undefined") {
					obj.patientIDs.push({personID:ind.personID, targetID:ind.targetID});
				}
			} else {
				if(typeof ind.rearr_gene1 !='undefined'){
					$scope.search.diseaseList.push({geneName:ind.rearr_gene1+' '+ind.rearr_gene2, patientIDs:[{personID:ind.personID, targetID:ind.targetID}]});
				} else {
					$scope.search.diseaseList.push({geneName:ind.geneName, patientIDs:[{personID:ind.personID, targetID:ind.targetID}]});
				}
			}
		}
	});
  }
  
  
  // Gene
  $scope.onSelectGene = function($item) {
	if(typeof $item!='undefined'){
		$scope.search.gene = $item;
	} else {
		$scope.search.gene= '';
	}
    
    var output =[];
    $scope.search.geneList={patientIDs:[]};
    
	angular.forEach($scope.allPatientsSearch, function(patient) {
	    if($scope.search.gene!=null && patient.geneName !=null && patient.geneName.toLowerCase() === $scope.search.gene.toLowerCase()){
            output.push(patient);
        }
	    if(patient.rearr_gene1 !=null && patient.rearr_gene1.toLowerCase() === $scope.search.gene.toLowerCase()){
	    	output.push(patient);
	    }
	    if(patient.rearr_gene2 !=null && patient.rearr_gene2.toLowerCase() === $scope.search.gene.toLowerCase()){
	    	output.push(patient);
	    }
	});
	angular.forEach(output, function(ind) {
		var type=ind.result.substring(0, ind.result.indexOf(':'));
		if($scope.search.unknownSig==true || ind.unknownSignificant=='false') {
		    if(typeof $scope.search.geneList.patientIDs != 'undefined') {
				var obj2 = $scope.search.geneList.patientIDs.filter(function(b) { 
					return (b['personID']===ind.personID && b['type']===type);
				})[0]
				if(typeof obj2 == "undefined") {
					$scope.search.geneList.patientIDs.push({personID:ind.personID, targetID:ind.targetID, type:type});
				}
			} else {
				$scope.search.geneList.patientIDs.push({personID:ind.personID, targetID:ind.targetID, type:type});
			}
		}
	});
  }
    
  // Clear search
  $scope.clearSearch = function() {
    $scope.search.disease = '';
    $scope.search.gene = '';
    $scope.search.geneList = {};
  }
  

  // Show the pages
  $scope.showPage = function(page, personID) {
    // Check if they are leaving the page without saving the meeting outcome
    $anchorScroll();
    if($scope.meeting.new) {
      var leavePage = confirm('You have an unsaved meeting outcome. Are you sure you want to leave the page?');
      if(leavePage) {
        alert('The latest meeting outcome will not be saved.')
        $scope.cancelMeetingOutcome();
      } else {
        return false;
      }
    }

    // Clear the significantMutations, ready for the next patient
    $scope.currentPatient.significantMutations = {
      tumourNGS: [],
      latestTumourNGS: [],
      ctDNA: [],
      fmTumour: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      fmBlood: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      latestCtDNA: [],
      latestFmTumour: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      latestFmBlood: {
    	  copyNumberAlteration: [],
    	  rearrangement: [],
    	  shortVariant: []
      },
      summery:[],
      meetingOutcomeSummaryCNA: {},
      meetingOutcomeSummaryR: {},
      meetingOutcomeSummarySV: {}
    };
    
    

    ////console.log(page, personID);
    $scope.currentPatient.person_id = personID;

    if(page == 'list') {
      $scope.page = {
        "list": true,
        "detail": false,
        "status": false,
        "genepanel": false,
        "admin": false,
        "search": false
      };
      $scope.cancelMeetingOutcome();
      $scope.getAllPatients();

    } else if(page == 'search'){
      $scope.page = {
        "list": false,
        "detail": false,
        "status": false,
        "genepanel": false,
        "admin": false,
        "search": true
      };
      $scope.cancelMeetingOutcome();
      $scope.getAllPatients();
    
    } else if(page == 'genepanel') {
        $scope.page = {
          "list": false,
          "detail": false,
          "status": false,
          "genepanel": true,
          "admin": false,
          "search": false
        };
        $scope.getAllGenes();
        $scope.getAllGenesPanel();
    } else if(page == 'admin') {
    	$scope.page = {
	        "list": false,
	        "detail": false,
	        "status": false,
	        "genepanel": false,
	        "admin": true,
	        "search": false
	    };

      // Fetch data
      $http({
        method: 'GET',
        url: 'api/data.jsp?endpoint=listadminusers&random='+Math.random()
      }).then(function successCallback(response) {
        $scope.admin.users = response.data;

      }, function errorCallback(response) {
        console.log('Error fetching list of admin users: '+response.status);
      });

    } else if(page == 'status') {
      $scope.page = {
        "list": false,
        "detail": false,
        "status": true,
        "admin": false,
        "search": false
      };

      // Fetch the system status data every 1 minute
      //setInterval(function() {
        $http({
          method: 'GET',
          url: 'api/data.jsp?endpoint=systemstatus'
        }).then(function successCallback(response) {
          //console.log('Fetched system status.');
          //console.log(response.data);

          $scope.system.status = response.data;

        }, function errorCallback(response) {
          //console.log('Error fetching system status: '+response.status);
        });
      //}, 30000);

    } else if(page == 'detail') {
//    	console.log('load page detail ' + personID);
    	$scope.timestamp = Date.now();
      // Get the patient details
      $http({
        method: 'GET',
        url: 'api/data.jsp?endpoint=patient&personID='+personID
      }).then(function successCallback(response) {
        //console.log('Fetched patient details.');
        ////console.log(response.data);

        // Update the model
        $scope.currentPatient.id = response.data.targetID;
        $scope.currentPatient.disease = response.data.conditionName;
        $scope.currentPatient.age = response.data.personAge;
        $scope.currentPatient.gender = response.data.genderName;
        $scope.currentPatient.site = response.data.site;
        $scope.currentPatient.dateConsent = response.data.consentDate;
        $scope.currentPatient.dateDiagnosis = response.data.conditionStartDate;
        //$scope.currentPatient.specimenDate1 = response.data.specimenDate1;
        $scope.currentPatient.editLockMessage = response.data.editLockMessage;
        $scope.currentPatient.trialFile = response.data.trialFile;
        $scope.currentPatient.qciFiles = response.data.qciFiles;
        $scope.currentPatient.rnaFiles = response.data.rnaFiles;

        $scope.print.targetID = response.data.targetID;
        $scope.print.consultant = response.data.consultant;
        

        // Change the view
        $scope.page = {
          "list": false,
          "detail": true,
          "status": false,
          "admin": false,
          "search": false
        };
        $scope.showQCIBaseline('');
        
      }, function errorCallback(response) {
//        console.log('Error fetching patient details: '+response.status);
      });

      // Get the treatment history / general information
      $scope.loadGeneralInfo(personID);
      
      // Get the tumour NGS details
      $scope.loadTumour(personID);

      // Get the NGS subset
      $scope.loadNGSSubset(personID);

      // Get the NGS exploratory subset
      $scope.loadNGSExploartory(personID);

      // Get the Foundation Medicine Tumour data 
      $scope.loadFMTumour(personID);
      
      // Get the Foundation Medicine Blood data
      $scope.loadFMBlood(personID);
      
      // Get IHC Report 
      $scope.loadIHCReport(personID);
      
      // Get configured genomics data
      $scope.loadGenomicsData(personID);
      
      // Patient PDX/CDX
      if($scope.pdxcdx=='true'){
	      $http({
	        method: 'GET',
	        url: 'api/data.jsp?endpoint=pdxcdx&personID='+personID,
	      }).then(function successCallback(response) {
	        $scope.pdxcdx = response.data;
	
	      }, function errorCallback(response) {
	//        console.log('Error fetching current user: '+response.status);
	      });
      }
      // Patient meeting outcome
      $http({
        method: 'GET',
        url: 'api/data.jsp?endpoint=getmeetingoutcome&personID='+personID
      }).then(function successCallback(response) {
        // Update the meeting outcome table
    	$scope.meeting.edits=0;
        $scope.meeting.outcome = response.data;

        $scope.getLatestMutationSelections();
        $scope.getLatestVersionMutationSelections();

      }, function errorCallback(response) {
//        console.log('Error fetching meeting outcome: '+response.status);
      });
      
      if(typeof $scope.genePanles=='undefined'){
    	// get gene penels
          $http({
            method: 'GET',
            url: 'rest/GenePanels'
          }).then(function successCallback(response) {
            $scope.genePanels = response.data;
          }, function errorCallback(response) {
//            console.log('Error fetching genePanels: '+response.status);
          });
      }
    }
    $scope.showTumourBaseline(1);
    $scope.showFMBloodBaseline(1);
    $scope.showFMTumourBaseline(1);
    $scope.showIHCBaseline(1);
  };

  $scope.showDetailSection = function(section) {
    // Check if they are leaving the page without saving the meeting outcome
    if($scope.meeting.new) {
      var leavePage = confirm('You have an unsaved meeting outcome. Are you sure you want to leave the page?');
      if(leavePage) {
        alert('The latest meeting outcome will not be saved.')
        $scope.cancelMeetingOutcome();
      } else {
        return false;
      }
    }

    // Hide everything
    $scope.detailSection.tumourNGS = false;
    $scope.detailSection.foundationTumour = false;
    $scope.detailSection.ctdnaNGS = false;
    $scope.detailSection.ctdnaExploratory = false;
    $scope.detailSection.foundationBlood = false;
    $scope.detailSection.pdxcdx = false;
    $scope.detailSection.meeting = false;
    $scope.detailSection.general = false;
    $scope.detailSection.addReports = false;
    $scope.detailSection.ihc = false;
    
    // Hide generic genomics section
    generic_li=angular.element('.generic-genomics-data');
	for(i=0;i<generic_li.length;i++){
		  name=generic_li[i].id;
		  if(name.startsWith("detail-sidebar-")){
			  source=name.slice(15);
		  }
		  
		  $scope.detailSection[source]=false;
		  $scope.detailSection[source+'Subs']={};
		  $scope.detailSection[source+'Subs'][1]=true;
		  for(j=2;j<24;j++){
			  $scope.detailSection[source+'Subs'][j]=false;
		  }
	}
    
    //resetTabs
    Object.keys($scope.currentPatient.ngsSubset.baseline).forEach(function(baseline, i) {
		Object.keys($scope.currentPatient.ngsSubset.baseline[baseline].runs).forEach(function(key,index) {
		    // key: the name of the object key
		    // index: the ordinal position of the key within the object 
		    $scope['active_ctdna'][baseline][key]=''+Object.keys($scope.currentPatient.ngsSubset.baseline[baseline].runs[key]).length;
		});
	});
    Object.keys($scope.currentPatient.exploratory.baseline).forEach(function(baseline, i) {
		Object.keys($scope.currentPatient.exploratory.baseline[baseline].runs).forEach(function(key,index) {
		    // key: the name of the object key
		    // index: the ordinal position of the key within the object 
		    $scope['active_exploratory'][baseline][key]=''+Object.keys($scope.currentPatient.exploratory.baseline[baseline].runs[key]).length;
		});
	});
    Object.keys($scope.currentPatient.tumourNGS.baseline).forEach(function(baseline, i) {
		Object.keys($scope.currentPatient.tumourNGS.baseline[baseline].runs).forEach(function(key,index) {
		    // key: the name of the object key
		    // index: the ordinal position of the key within the object 
		    $scope['active_tumour'][baseline][key]=''+Object.keys($scope.currentPatient.tumourNGS.baseline[baseline].runs[key]).length;
		});
	});
    //$scope.showBaseline(1);
    jQuery('#detail-sidebar ul li').removeClass('detail-sidebar-selected');
    
    $scope.detailSection[section] = true;
    jQuery('#detail-sidebar-'+section).addClass('detail-sidebar-selected');
    
   
  };

  $scope.addMeetingOutcome = function() {
    ////console.log('addMeetingOutcome');
    $http({
		  method: 'GET',
		  url: 'api/data.jsp?endpoint=islocked&personID='+$scope.currentPatient.person_id
	  }).then(function successCallback(response) {
		  if(response.data.success == "true" && response.data.locked =="true") {
			  $scope.showPage('detail', $scope.currentPatient.person_id);
		  } else if(response.data.success == "true" && response.data.locked =="false") {
			  $scope.meeting.new = true;
		  }
    });
  };

  $scope.saveMeetingOutcome = function() {
    ////console.log($scope.meeting.add);

	if($scope.meeting.flag) {
		return false;
	}  
	  
    // Validate input
    if($scope.meeting.add.outcome == '') {
      alert('You must complete the meeting outcome dropdown.');
      return false;
    }

    if($scope.meeting.add.date == '') {
      alert('You must select a date.');
      return false;
    }

    $scope.meeting.flag=true;
    /*
    if(!confirm("If you have selected any significant mutations please ensure that they are correct as they cannot be edited in the meeting outcome once it has been saved. Do you want to continue?")) {
        event.preventDefault();
        return false;
    }
    */

    // If multiple meeting outcomes selected, convert to a string (with newlines)
    if(Array.isArray($scope.meeting.add.outcome)) {
      var meetingOutcomeArray = $scope.meeting.add.outcome;
      $scope.meeting.add.outcome = meetingOutcomeArray.join("\n");
    }
    var genericGenomic={};
    Object.keys($scope.currentPatient.significantMutations.latestGenericGenomic).map(function(key, index) {
    	genericGenomic[key] = {copyNumberAlteration:[], shortVariant:[], rearrangement:[]}
    	genericGenomic[key].copyNumberAlteration= $scope.currentPatient.significantMutations.latestGenericGenomic[key].copyNumberAlteration.map(function(a) {
			  return a.gene_variant_id;
		  });
    	genericGenomic[key].shortVariant= $scope.currentPatient.significantMutations.latestGenericGenomic[key].shortVariant.map(function(a) {
			  return a.gene_variant_id;
		  });
    	genericGenomic[key].rearrangement= $scope.currentPatient.significantMutations.latestGenericGenomic[key].rearrangement.map(function(a) {
			  return a.gene_variant_id;
		  });
	  });
    // Send to API
    $http({
      method: 'POST',
      url: 'api/data.jsp?endpoint=postmeetingoutcome',
      data: $.param({ 
    	  personID: $scope.currentPatient.person_id, 
    	  meetingDate: $scope.meeting.add.date, 
    	  outcome: $scope.meeting.add.outcome, 
    	  notes: $scope.meeting.add.notes, 
    	  ctDNA: angular.toJson($scope.currentPatient.significantMutations.latestCtDNA.map(function(a) {
    		  return a.geneVarientID;
    	  })), 
    	  tumourNGS: angular.toJson($scope.currentPatient.significantMutations.latestTumourNGS.map(function(a) {
    		  return a.geneVarientID;
    	  })),
    	  fmTumour: angular.toJson({
    		  copyNumberAlteration: $scope.currentPatient.significantMutations.latestFmTumour.copyNumberAlteration.map(function(a) {
    			  return a.gene_variant_id;
    		  }),
    		  shortVariant: $scope.currentPatient.significantMutations.latestFmTumour.shortVariant.map(function(a) {
    			  return a.gene_variant_id;
    		  }),
    		  rearrangement: $scope.currentPatient.significantMutations.latestFmTumour.rearrangement.map(function(a) {
    			  return a.gene_variant_id;
    		  })
    	  }),
    	  fmBlood: angular.toJson({
    		  copyNumberAlteration: $scope.currentPatient.significantMutations.latestFmBlood.copyNumberAlteration.map(function(a) {
    			  return a.gene_variant_id;
    		  }),
    		  shortVariant: $scope.currentPatient.significantMutations.latestFmBlood.shortVariant.map(function(a) {
    			  return a.gene_variant_id;
    		  }),
    		  rearrangement: $scope.currentPatient.significantMutations.latestFmBlood.rearrangement.map(function(a) {
    			  return a.gene_variant_id;
    		  })
    	  }),
    	  genericGenomic: angular.toJson(genericGenomic)
      
      }),
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).then(function successCallback(response) {
      //console.log('Meeting outcome updated.');

      // Show success message
      $scope.meeting.message = 'Meeting outcome updated.';

      $timeout( function() {
        $scope.meeting.message = '';
      }, 10000);

      // Add new row data
      var bloodSpecimenDate;
      var tumourSpecimenDate;
      if(typeof $scope.currentPatient.significantMutations.latestCtDNA[0] != 'undefined'){
    	  bloodSpecimenDate=$scope.currentPatient.significantMutations.latestCtDNA[0].specimenDate;
      } else if(typeof $scope.currentPatient.treatments.bloodSamples[0] != 'undefined'){
    	  bloodSpecimenDate=$scope.currentPatient.treatments.bloodSamples[$scope.currentPatient.treatments.bloodSamples.length-1].dateBlood;
      } else {
    	  bloodSpecimenDate='no blood sample';
      }
      if(typeof $scope.currentPatient.significantMutations.latestTumourNGS[0] != 'undefined'){
    	  tumourSpecimenDate=$scope.currentPatient.significantMutations.latestTumourNGS[0].specimenDate;
      } else if(typeof $scope.currentPatient.treatments.tumourSamples[0] != 'undefined'){
    	  tumourSpecimenDate=$scope.currentPatient.treatments.tumourSamples[$scope.currentPatient.treatments.tumourSamples.length-1].dateTumour;
      } else {
    	  tumourSpecimenDate='no tumour sample';
      }
      var copy = angular.copy($scope.currentPatient.significantMutations.latestGenericGenomic);
      angular.forEach(copy, function(v, k) {
		  if (v.hasOwnProperty('shortVariant')) {
		      v[k+"SV"] = v['shortVariant'];
		      delete v['shortVariant'];
		  } 
		  if (v.hasOwnProperty('rearrangement')) {
		      v[k+'R'] = v['rearrangement'];
		      delete v['rearrangement'];
		  } 
		  if (v.hasOwnProperty('copyNumberAlteration')) {
	          v[k+'CNA'] = v['copyNumberAlteration'];
	          delete v['copyNumberAlteration'];
	      }
		  console.log(v)
      });
      
      $scope.meeting.outcome.unshift({
    	meeting_id: response.data.mo_id,
        meetingDate: $scope.meeting.add.date,
        outcome: $scope.meeting.add.outcome,
        notes: $scope.meeting.add.notes,
        //geneTables: $scope.currentPatient.significantMutations,
        summary: $scope.currentPatient.significantMutations.summery.slice(0),
        ctDNA: $scope.currentPatient.significantMutations.latestCtDNA.slice(0),
        tumourNGS: $scope.currentPatient.significantMutations.latestTumourNGS.slice(0),
        fmBlood: { 
        	FMBloodSV: $scope.currentPatient.significantMutations.latestFmBlood.shortVariant.slice(0),
        	FMBloodR: $scope.currentPatient.significantMutations.latestFmBlood.rearrangement.slice(0),
        	FMBloodCNA: $scope.currentPatient.significantMutations.latestFmBlood.copyNumberAlteration.slice(0)
        },
        fmTumour: {
        	FMTumourSV: $scope.currentPatient.significantMutations.latestFmTumour.shortVariant.slice(0),
        	FMTumourR: $scope.currentPatient.significantMutations.latestFmTumour.rearrangement.slice(0),
        	FMTumourCNA: $scope.currentPatient.significantMutations.latestFmTumour.copyNumberAlteration.slice(0)
        },
        genericGenomic: Object.assign({}, copy),
        lastUpdated: '',
        lastPrinted: '',
        ctDNASampleDate: bloodSpecimenDate,
        tumourSampleDate: tumourSpecimenDate,
        updatedBy: $scope.currentUser.userID
      });

      $scope.meeting.add.outcome = '';
      $scope.meeting.add.notes = '';
      picker.setDate(moment().toDate());

      $scope.meeting.new = false;

      // Clear the significantMutations
      //$scope.currentPatient.significantMutations.meetingOutcomeSummary = {};

    }, function errorCallback(response) {
      //console.log('Error fetching meeting outcome: '+response.status);

      $scope.meeting.message = '';
      $scope.showPage('detail', $scope.currentPatient.person_id);
      if(typeof response.data.error!= "undefined"){
    	  $scope.currentPatient.editLockMessage = response.data.error;
      } else {
	      // Show error message
	      alert('Error: Unable to update the meeting outcome.');
	  }
    }).finally(function (){
    	$scope.meeting.flag=false;
    });

  };

  $scope.cancelMeetingOutcome = function() {
    ////console.log('cancelMeetingOutcome');
    $scope.meeting.new = false;
    //$scope.meeting.add.date = moment().toDate();
    picker.setDate(moment().toDate());
    $scope.meeting.add.outcome = '';
    $scope.meeting.add.notes = '';
  };

  $scope.printMeetingOutcome = function(meeting,index) {
	$scope.print.index = index;
    $scope.print.mtbDate = meeting.meetingDate;
    $scope.print.outcome = meeting.outcome;
    $scope.print.notes = meeting.notes;
    $scope.print.datePrinted = moment().format('DD/MM/YYYY HH:mm:ss');
    $scope.print.updatedBy = meeting.updatedBy;
    $scope.print.lastUpdated = meeting.lastUpdated;
    $scope.print.ctDNASampleDate = meeting.ctDNASampleDate;
    $scope.print.tumourSampleDate = meeting.tumourSampleDate;    
    
    //console.log(meeting.geneTables.ctDNA);
    //console.log(meeting.geneTables.tumourNGS);

    // ctDNA sample date
    //$scope.print.ctDNASampleDate = $scope.currentPatient.ngsSubset.specimenDate;

    // Tumour sample date
    //$scope.print.tumourSampleDate = $scope.currentPatient.tumourNGS.baseline['1'].specimen.specimenDate;

    // Gene tables
    if(typeof meeting.summary != "undefined") {
    	$scope.print.summary = meeting.summary;
    } else {
    	$scope.print.summary = [];
    }
    if(typeof meeting.tumourNGS != "undefined") {
//    	if(typeof meeting.geneTables.latestTumourNGS != "undefined"){
//    		$scope.print.tumourNGS = meeting.geneTables.latestTumourNGS;
//    	} else {
    		$scope.print.tumourNGS = meeting.tumourNGS;
//    	}
    } else {
    	$scope.print.tumourNGS = [];
    }

    if(typeof meeting.ctDNA != "undefined") {
//    	if(typeof meeting.geneTables.latestCtDNA != "undefined"){
//    		$scope.print.ctDNA = meeting.geneTables.latestCtDNA;
//    	} else {
    		$scope.print.ctDNA = meeting.ctDNA;
//    	}
    } else {
    	$scope.print.ctDNA = [];
    }
    
    if(typeof meeting.fmBlood != "undefined") {
    	$scope.print.fmBlood = meeting.fmBlood;
    } else {
    	$scope.print.fmBlood = {};
    }
    
    if(typeof meeting.fmTumour != "undefined") {
    	$scope.print.fmTumour = meeting.fmTumour;
    } else {
    	$scope.print.fmTumour = {};
    }
    //post print datetime
    $http({
        method: 'POST',
        url: 'api/data.jsp?endpoint=printmeetingoutcome',
        data: $.param({ meetingId: meeting.meeting_id }),
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      }).then(function successCallback(response) {
    	  if(response.data.success == 'true'){
    		  $scope.print.datePrinted = response.data.lastPrinted;
    		  $scope.meeting.outcome[index].lastPrinted = response.data.lastPrinted;
    		  if(typeof $scope.meeting.outcome[index].needPrinting != "undefined"){
    		  	$scope.meeting.outcome[index].needPrinting = false;
    	  	  }
    	  }

        $timeout( function() {
          $scope.meeting.message = '';
        }, 10000);
      });

    //$scope.$apply();
    $timeout( function(){
      window.print();
    }, 500);

    //window.print();
  };

  window.onbeforeunload = function() {
    if($scope.meeting.new) {
      return "You have an unsaved meeting outcome. Are you sure you want to leave the page?";
    }
  };

  $scope.$on('$locationChangeStart', function(event, next, current) {
    if($scope.meeting.new) {
      if(!confirm("You have an unsaved meeting outcome. Are you sure you want to leave this page?")) {
          event.preventDefault();
      }
    }
  });

  $scope.showQCIBaseline = function(key) {
	  keys=Object.keys($scope.detailSection.qciSubs);
	  if(keys.length ==0)
		  return;
	  for(var i in keys){
		  $scope.detailSection.qciSubs[keys[i]] = false;
	  }
	  if(typeof key == 'undefined' || key==''){
		  $scope.detailSection.qciSubs[keys[0]] = true;
	  } else {
		  $scope.detailSection.qciSubs[key] = true;
	  }
	  return false;
  };
  
  $scope.showIHCBaseline = function(key) {
	  	if(typeof key == 'undefined'){
	  		return false;
	  	}
	    for(var i in Object.keys($scope.detailSection.ihcSubs)) {
	      $scope.detailSection.ihcSubs[i] = false;
	    }
	    $scope.detailSection.ihcSubs[key] = true;
	    return false;
	  };
  $scope.showTumourBaseline = function(key) {
	  	if(typeof key == 'undefined'){
	  		return false;
	  	}
	    for(var i in Object.keys($scope.detailSection.tumourSubs)) {
	      $scope.detailSection.tumourSubs[i] = false;
	    }
	    $scope.detailSection.tumourSubs[key] = true;
	    
	    return false;
	  };
	  
  $scope.showFMTumourBaseline = function(key) {
	  	if(typeof key == 'undefined'){
	  		return false;
	  	}
	    for(var i in Object.keys($scope.detailSection.fmTumourSubs)) {
	      $scope.detailSection.fmTumourSubs[i] = false;
	    }
	    $scope.detailSection.fmTumourSubs[key] = true;
	    return false;
  };
  
  $scope.showBaseline = function(type, key) {
//	  console.log(type +' ' + key);
	  if(typeof key == 'undefined' || typeof type == 'undefined' ){
		  return false;
	  }
    for(var i in Object.keys($scope.detailSection[type+'Subs'])) {
      $scope.detailSection[type+'Subs'][i] = false;
    }
    $scope.detailSection[type+'Subs'][key] = true;
    
    return false;
  };
  
  $scope.showFMBloodBaseline = function(key) {
    for(var i in Object.keys($scope.detailSection.fmBloodSubs)) {
      $scope.detailSection.fmBloodSubs[i] = false;
    }
    $scope.detailSection.fmBloodSubs[key] = true;
    
    return false;
  };

  $scope.tumourNGSSelection = function(geneVarientId, geneName, result, mutant, specimenDate, checked, timepoint) {
    //console.log(checked);
    if(checked) {
      // Add to the table
      result = result.replace("<strong>", "");
      result = result.replace("</strong>", "");
      $scope.currentPatient.significantMutations.tumourNGS.push({geneName: geneName, result: result, mutantAllele: mutant, specimenDate: specimenDate, geneVarientID: geneVarientId});
      $scope.currentPatient.significantMutations.latestTumourNGS.push({geneName: geneName, result: result, mutantAllele: mutant, specimenDate: specimenDate, geneVarientID: geneVarientId});
      $scope.currentPatient.significantMutations.summery.push({gene: geneName, description: result, source: 'Tumour NGS', type: 'Short Variant', geneVarientID: geneVarientId, timepoint: timepoint});
      $scope.addMutationSelection(geneVarientId,'NGS');
      $scope.updateMeetingOutcomeSummary(geneName, result, specimenDate, true, 'NGS');
    } else {
      // Remove from the table
      for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.tumourNGS).length; i++) {
        if($scope.currentPatient.significantMutations.tumourNGS[i].geneVarientID == geneVarientId) {
          $scope.currentPatient.significantMutations.tumourNGS.splice(i, 1);
        }
      }
      for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.latestTumourNGS).length; i++) {
          if($scope.currentPatient.significantMutations.latestTumourNGS[i].geneVarientID == geneVarientId) {
            $scope.currentPatient.significantMutations.latestTumourNGS.splice(i, 1);
          }
      }
      for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.summery).length; i++) {
    	  if($scope.currentPatient.significantMutations.summery[i].geneVarientID == geneVarientId) {
    		  $scope.currentPatient.significantMutations.summery.splice(i,1);
    	  }
      }
      $scope.deleteMutationSelection(geneVarientId,'NGS');
      $scope.updateMeetingOutcomeSummary(geneName, result, specimenDate, false, 'NGS');
    }
  };
  
  $scope.fmBloodSelection = function(geneObject, specimenDate, checked, timepoint) {
	    if(checked) {
	      // Add to the table
	      if(geneObject.variation_type=='short_variant'){
	    	  $scope.currentPatient.significantMutations.fmBlood.shortVariant.push(geneObject);
	    	  $scope.addMutationSelection(geneObject.gene_variant_id,'FMBloodSV');
	    	  $scope.currentPatient.significantMutations.latestFmBlood.shortVariant.push(geneObject);
	    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.geneName, description: geneObject.result, source: 'FM Blood', type: 'Short Variant', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
	    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.result, specimenDate, true, 'FMBloodSV');
	      }	else if(geneObject.variation_type=='rearrangement'){
	    	  $scope.currentPatient.significantMutations.fmBlood.rearrangement.push(geneObject);
	    	  $scope.addMutationSelection(geneObject.gene_variant_id, 'FMBloodR');
	    	  $scope.currentPatient.significantMutations.latestFmBlood.rearrangement.push(geneObject);
	    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.gene1+' - '+geneObject.gene2, description: geneObject.description, source: 'FM Blood', type: 'Rearrangement', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
	    	  $scope.updateMeetingOutcomeSummary(geneObject.gene1, geneObject.gene2, specimenDate, true, 'FMBloodR');
	      } else if(geneObject.variation_type=='copy_number_alteration'){
	    	  $scope.currentPatient.significantMutations.fmBlood.copyNumberAlteration.push(geneObject);
	    	  $scope.addMutationSelection(geneObject.gene_variant_id, 'FMBloodCNA');
	    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.geneName, description: geneObject.type, source: 'FM Blood', type: 'Copy Number Alteration', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
	    	  $scope.currentPatient.significantMutations.latestFmBlood.copyNumberAlteration.push(geneObject);
	    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.type, specimenDate, true, 'FMBloodCNA');
	      }
	    } else {
	      // Remove from the table
	    	if(geneObject.variation_type=='short_variant'){
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.fmBlood.shortVariant.length; i++) {
			        if($scope.currentPatient.significantMutations.fmBlood.shortVariant[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.fmBlood.shortVariant.splice(i, 1);
			        }
				  }
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestFmBlood.shortVariant.length; i++) {
			        if($scope.currentPatient.significantMutations.latestFmBlood.shortVariant[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.latestFmBlood.shortVariant.splice(i, 1);
			        }
				  }
		    	  $scope.deleteMutationSelection(geneObject.gene_variant_id,'FMBloodSV');
		    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.result, specimenDate, false, 'FMBloodSV');
		      }	else if(geneObject.variation_type=='rearrangement'){
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.fmBlood.rearrangement.length; i++) {
			        if($scope.currentPatient.significantMutations.fmBlood.rearrangement[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.fmBlood.rearrangement.splice(i, 1);
			        }
			      }
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestFmBlood.rearrangement.length; i++) {
			        if($scope.currentPatient.significantMutations.latestFmBlood.rearrangement[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.latestFmBlood.rearrangement.splice(i, 1);
			        }
			      }
		    	  $scope.deleteMutationSelection(geneObject.gene_variant_id,'FMBloodR');
		    	  $scope.updateMeetingOutcomeSummary(geneObject.gene1, geneObject.gene2, specimenDate, false, 'FMBloodR');  
		      }
		      else if(geneObject.variation_type=='copy_number_alteration'){
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.fmBlood.copyNumberAlteration.length; i++) {
			        if($scope.currentPatient.significantMutations.fmBlood.copyNumberAlteration[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.fmBlood.copyNumberAlteration.splice(i, 1);
			        }
			      }
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestFmBlood.copyNumberAlteration.length; i++) {
			        if($scope.currentPatient.significantMutations.latestFmBlood.copyNumberAlteration[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.latestFmBlood.copyNumberAlteration.splice(i, 1);
			        }
			      }
		    	  $scope.deleteMutationSelection(geneObject.gene_variant_id,'FMBloodCNA');
		    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.type, specimenDate, false, 'FMBloodCNA'); 
		      }
	    	for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.summery).length; i++) {
		    	  if($scope.currentPatient.significantMutations.summery[i].geneVarientID == geneObject.gene_variant_id) {
		    		  $scope.currentPatient.significantMutations.summery.splice(i,1);
		    	  }
		      }
		    }
	  };

	  $scope.fmTumourSelection = function(geneObject, specimenDate, checked, timepoint) {
		    if(checked) {
		      // Add to the table
		      if(geneObject.variation_type=='short_variant'){
		    	  $scope.currentPatient.significantMutations.fmTumour.shortVariant.push(geneObject);
		    	  $scope.currentPatient.significantMutations.latestFmTumour.shortVariant.push(geneObject);
		    	  $scope.addMutationSelection(geneObject.gene_variant_id,'FMTumourSV');
		    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.geneName, description: geneObject.result, source: 'FM Tumour', type: 'Short Variant', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
		    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.result, specimenDate, true, 'FMTumourSV');
		      }	else if(geneObject.variation_type=='rearrangement'){
		    	  $scope.currentPatient.significantMutations.fmTumour.rearrangement.push(geneObject);
		    	  $scope.currentPatient.significantMutations.latestFmTumour.rearrangement.push(geneObject);
		    	  $scope.addMutationSelection(geneObject.gene_variant_id, 'FMTumourR');
		    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.gene1+' - '+geneObject.gene2, description: geneObject.description, source: 'FM Tumour', type: 'Rearrangement', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
		    	  $scope.updateMeetingOutcomeSummary(geneObject.gene1, geneObject.gene2, specimenDate, true, 'FMTumourR');
		      } else if(geneObject.variation_type=='copy_number_alteration'){
		    	  $scope.currentPatient.significantMutations.fmTumour.copyNumberAlteration.push(geneObject);
		    	  $scope.currentPatient.significantMutations.latestFmTumour.copyNumberAlteration.push(geneObject);
		    	  $scope.addMutationSelection(geneObject.gene_variant_id, 'FMTumourCNA');
		    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.geneName, description: geneObject.type, source: 'FM Tumour', type: 'Copy Number Alteration', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
		    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.type, specimenDate, true, 'FMTumourCNA');
		      }
		    } else {
		      // Remove from the table
		      if(geneObject.variation_type=='short_variant'){
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestFmTumour.shortVariant.length; i++) {
			        if($scope.currentPatient.significantMutations.latestFmTumour.shortVariant[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.latestFmTumour.shortVariant.splice(i, 1);
			        }
				  }
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.fmTumour.shortVariant.length; i++) {
			        if($scope.currentPatient.significantMutations.fmTumour.shortVariant[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.fmTumour.shortVariant.splice(i, 1);
			        }
				  }
		    	  $scope.deleteMutationSelection(geneObject.gene_variant_id,'FMTumourSV');
		    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.result, specimenDate, false, 'FMTumourSV');
		      }	else if(geneObject.variation_type=='rearrangement'){
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.fmTumour.rearrangement.length; i++) {
			        if($scope.currentPatient.significantMutations.fmTumour.rearrangement[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.fmTumour.rearrangement.splice(i, 1);
			        }
			      }
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestFmTumour.rearrangement.length; i++) {
			        if($scope.currentPatient.significantMutations.latestFmTumour.rearrangement[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.latestFmTumour.rearrangement.splice(i, 1);
			        }
			      }
		    	  $scope.deleteMutationSelection(geneObject.gene_variant_id,'FMTumourR');
		    	  $scope.updateMeetingOutcomeSummary(geneObject.gene1, geneObject.gene2, specimenDate, false, 'FMTumourR');  
		      }
		      else if(geneObject.variation_type=='copy_number_alteration'){
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.fmTumour.copyNumberAlteration.length; i++) {
			        if($scope.currentPatient.significantMutations.fmTumour.copyNumberAlteration[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.fmTumour.copyNumberAlteration.splice(i, 1);
			        }
			      }
		    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestFmTumour.copyNumberAlteration.length; i++) {
			        if($scope.currentPatient.significantMutations.latestFmTumour.copyNumberAlteration[i].gene_variant_id == geneObject.gene_variant_id) {
			          $scope.currentPatient.significantMutations.latestFmTumour.copyNumberAlteration.splice(i, 1);
			        }
			      }
		    	  $scope.deleteMutationSelection(geneObject.gene_variant_id,'FMTumourCNA');
		    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.type, specimenDate, false, 'FMTumourCNA'); 
		      }
		      for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.summery).length; i++) {
		    	  if($scope.currentPatient.significantMutations.summery[i].geneVarientID == geneObject.gene_variant_id) {
		    		  $scope.currentPatient.significantMutations.summery.splice(i,1);
		    	  }
		      }
		    }
		  };

		  $scope.genericGenomicSelection = function(type, geneObject, specimenDate, checked, timepoint) {
			    console.log(type);
			    console.log(geneObject);
			    console.log(checked);
			    if(checked) {
			      // Add to the table
			      if(geneObject.variation_type=='short_variant'){
			    	  $scope.currentPatient.significantMutations.genericGenomic[type].shortVariant.push(geneObject);
			    	  $scope.addMutationSelection(geneObject.gene_variant_id, type+'SV');
			    	  $scope.currentPatient.significantMutations.latestGenericGenomic[type].shortVariant.push(geneObject);
			    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.geneName, description: geneObject.result, source: type, type: 'Short Variant', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
			    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.result, specimenDate, true, type+'SV');
			      }	else if(geneObject.variation_type=='rearrangement'){
			    	  $scope.currentPatient.significantMutations.genericGenomic[type].rearrangement.push(geneObject);
			    	  $scope.addMutationSelection(geneObject.gene_variant_id, type+'R');
			    	  $scope.currentPatient.significantMutations.latestGenericGenomic[type].rearrangement.push(geneObject);
			    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.gene1+' - '+geneObject.gene2, description: geneObject.description, source: type, type: 'Rearrangement', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
			    	  $scope.updateMeetingOutcomeSummary(geneObject.gene1, geneObject.gene2, specimenDate, true, type+'R');
			      } else if(geneObject.variation_type=='copy_number_alteration'){
			    	  $scope.currentPatient.significantMutations.genericGenomic[type].copyNumberAlteration.push(geneObject);
			    	  $scope.addMutationSelection(geneObject.gene_variant_id, type+'CNA');
			    	  $scope.currentPatient.significantMutations.summery.push({gene: geneObject.geneName, description: geneObject.type, source: type, type: 'Copy Number Alteration', geneVarientID: geneObject.gene_variant_id, timepoint: timepoint});
			    	  $scope.currentPatient.significantMutations.latestGenericGenomic[type].copyNumberAlteration.push(geneObject);
			    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.type, specimenDate, true, type+'CNA');
			      }
			    } else {
			      // Remove from the table
			    	if(geneObject.variation_type=='short_variant'){
				    	  for(var i=0; i<$scope.currentPatient.significantMutations.genericGenomic[type].shortVariant.length;i++) {
			    			  if($scope.currentPatient.significantMutations.genericGenomic[type].shortVariant[i].gene_variant_id == geneObject.gene_variant_id) {
			    				  $scope.currentPatient.significantMutations.genericGenomic[type].shortVariant.splice(i, 1);
						        }
			    		  }
				    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestGenericGenomic[type].shortVariant.length; i++) {
						        if($scope.currentPatient.significantMutations.latestGenericGenomic[type].shortVariant[i].gene_variant_id == geneObject.gene_variant_id) {
						          $scope.currentPatient.significantMutations.latestGenericGenomic[type].shortVariant.splice(i, 1);
						        }
							  }
					    	  
				    	  $scope.deleteMutationSelection(geneObject.gene_variant_id, type+'SV');
				    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.result, specimenDate, false, type+'SV');
				    	  

				    	  
				      }	else if(geneObject.variation_type=='rearrangement'){
				    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestGenericGenomic[type].rearrangement.length; i++) {
					        if($scope.currentPatient.significantMutations.latestGenericGenomic[type].rearrangement[i].gene_variant_id == geneObject.gene_variant_id) {
					          $scope.currentPatient.significantMutations.latestGenericGenomic[type].rearrangement.splice(i, 1);
					        }
					      }
			    		  for(var i=0; i<$scope.currentPatient.significantMutations.genericGenomic[type].rearrangement.length;i++) {
			    			  if($scope.currentPatient.significantMutations.genericGenomic[type].rearrangement[i].gene_variant_id == geneObject.gene_variant_id) {
			    				  $scope.currentPatient.significantMutations.genericGenomic[type].rearrangement.splice(i, 1);
						        }
			    		  }
				    	  $scope.deleteMutationSelection(geneObject.gene_variant_id, type+'R');
				    	  $scope.updateMeetingOutcomeSummary(geneObject.gene1, geneObject.gene2, specimenDate, false, type+'R');  
				      }
				      else if(geneObject.variation_type=='copy_number_alteration'){
				    	  for(var i=0; i<$scope.currentPatient.significantMutations.latestGenericGenomic[type].copyNumberAlteration.length; i++) {
					        if($scope.currentPatient.significantMutations.latestGenericGenomic[type].copyNumberAlteration[i].gene_variant_id == geneObject.gene_variant_id) {
					          $scope.currentPatient.significantMutations.latestGenericGenomic[type].copyNumberAlteration.splice(i, 1);
					        }
					      }
			    		  for(var i=0; i<$scope.currentPatient.significantMutations.genericGenomic[type].copyNumberAlteration.length;i++) {
			    			  if($scope.currentPatient.significantMutations.genericGenomic[type].copyNumberAlteration[i].gene_variant_id == geneObject.gene_variant_id) {
			    				  $scope.currentPatient.significantMutations.genericGenomic[type].copyNumberAlteration.splice(i, 1);
						        }
			    		  }
				    	  $scope.deleteMutationSelection(geneObject.gene_variant_id, type+'CNA');
				    	  $scope.updateMeetingOutcomeSummary(geneObject.geneName, geneObject.type, specimenDate, false, type+'CNA'); 
				      }
			    	for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.summery).length; i++) {
				    	  if($scope.currentPatient.significantMutations.summery[i].geneVarientID == geneObject.gene_variant_id) {
				    		  $scope.currentPatient.significantMutations.summery.splice(i,1);
				    	  }
				      }
				    }
			  };

		  
		  
  $scope.ctDNASelection = function(geneVarientId, geneName, result, cfdnaFrequency, cfdnaReads, germlineFrequency, mutationType, highConfidence, specimenDate, checked, baseline) {
    //console.log(checked);
    if(checked) {
      // Add to the table
      result = result.replace("<strong>", "");
      result = result.replace("</strong>", "");
      var addData = {
          geneName: geneName,
          result: result,
          cfdnaFrequency: cfdnaFrequency,
          cfdnaReads: cfdnaReads,
          germlineFrequency: germlineFrequency,
          mutationType: mutationType,
          highConfidence: highConfidence,
          specimenDate: specimenDate,
          geneVarientID : geneVarientId,
          timepoint: baseline
        };
      $scope.currentPatient.significantMutations.ctDNA.push(addData);
      $scope.currentPatient.significantMutations.latestCtDNA.push(addData);
      $scope.currentPatient.significantMutations.summery.push({gene: geneName, description: result, source: 'ctDNA', type: 'Short Variant', geneVarientID: geneVarientId, timepoint: baseline});
      $scope.addMutationSelection(geneVarientId,'CTDNA');
      $scope.updateMeetingOutcomeSummary(geneName, result, specimenDate, true, 'CTDNA');
    } else {
      // Remove from the table
      for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.ctDNA).length; i++) {
        if($scope.currentPatient.significantMutations.ctDNA[i].geneVarientID == geneVarientId) {
          $scope.currentPatient.significantMutations.ctDNA.splice(i, 1);
        }
      }
      for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.latestCtDNA).length; i++) {
          if($scope.currentPatient.significantMutations.latestCtDNA[i].geneVarientID == geneVarientId) {
            $scope.currentPatient.significantMutations.latestCtDNA.splice(i, 1);
          }
      }
      for(var i=0; i<Object.keys($scope.currentPatient.significantMutations.summery).length; i++) {
    	  if($scope.currentPatient.significantMutations.summery[i].geneVarientID == geneVarientId) {
    		  $scope.currentPatient.significantMutations.summery.splice(i,1);
    	  }
      }
      $scope.deleteMutationSelection(geneVarientId,'CTDNA');
      $scope.updateMeetingOutcomeSummary(geneName, result, specimenDate, false, 'CTDNA');
    }
  };

  $scope.addMutationSelection = function(geneVarientId, ngsORctdna) {
     $http({
      method: 'POST',
      url: 'api/data.jsp?endpoint=addmutationselection',
      data: $.param({ personID: $scope.currentPatient.person_id, geneVarientId: geneVarientId, ngsOrctdna: ngsORctdna}),
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).then(function successCallback(response) {
    	 $scope.getLatestVersionMutationSelections();
      // Success
    }, function errorCallback(response) {
      // Error
      console.log('Error: Unable to post the mutation selection.');
      $scope.showPage('detail', $scope.currentPatient.person_id);
      if(typeof response.data.error!= "undefined"){
    	  $scope.currentPatient.editLockMessage = response.data.error;
      }
    });
  };
  
  $scope.deleteMutationSelection = function(geneVarientId, ngsORctdna){
    $http({
      method: 'POST',
      url: 'api/data.jsp?endpoint=deletemutationselection',
      data: $.param({ personID: $scope.currentPatient.person_id, geneVarientId: geneVarientId, ngsOrctdna: ngsORctdna}),
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).then(function successCallback(response) {
    	 $scope.getLatestVersionMutationSelections();
      // Success
    }, function errorCallback(response) {
      // Error
      console.log('Error: Unable to post the mutation selection.');
      $scope.showPage('detail', $scope.currentPatient.person_id);
      if(typeof response.data.error!= "undefined"){
    	  $scope.currentPatient.editLockMessage = response.data.error;
      }
    });
  };

  $scope.updateMeetingOutcomeSummary = function(geneName, result, specimenDate, addEntry, type) {
    var mosEntry = geneName+result+specimenDate+type;
    mosEntry = mosEntry.replace(/[^A-Za-z0-9]/g, '');
    mosEntry = mosEntry.replace(/strong/g, '');
    console.log('updateMeetingOutcomeSummery ' + type);
    if(addEntry) {
    	if(type=='CTDNA' || type=='NGS' || type.endsWith('SV')){
    		$scope.currentPatient.significantMutations.meetingOutcomeSummarySV[mosEntry] = geneName+' '+result;
    	} else if(type.endsWith('R')) {
    		$scope.currentPatient.significantMutations.meetingOutcomeSummaryR[mosEntry] = geneName+' '+result;
    	} else if (type.endsWith('CNA')){
    		$scope.currentPatient.significantMutations.meetingOutcomeSummaryCNA[mosEntry] = geneName+' '+result;
    	}
    } else {
    	if((type=='CTDNA' || type=='NGS' || type.endsWith('SV')) && typeof $scope.currentPatient.significantMutations.meetingOutcomeSummarySV[mosEntry] != 'undefined'){
    		delete $scope.currentPatient.significantMutations.meetingOutcomeSummarySV[mosEntry];
    	} else if((type.endsWith('R')) && typeof $scope.currentPatient.significantMutations.meetingOutcomeSummaryR[mosEntry] != 'undefined') {
    		delete $scope.currentPatient.significantMutations.meetingOutcomeSummaryR[mosEntry];
    	} else if((type.endsWith('CNA'))&& typeof $scope.currentPatient.significantMutations.meetingOutcomeSummaryCNA[mosEntry] != 'undefined') {
    		delete $scope.currentPatient.significantMutations.meetingOutcomeSummaryCNA[mosEntry];
    	}
    }
  };

  $scope.showSavedMutations = function(geneTables) {
    // Load saved mutations into the meeting outcome tables on click of meeting outcome row


  };

  $scope.isInMeetingOutcome = function(geneVarientID, geneName, geneResult, specimenDate, type) {
    if(type=='CTDNA') {
    	return $scope.currentPatient.significantMutations.ctDNA.some(function(item) {return item.geneVarientID === geneVarientID});
    } else if(type=='NGS') {
    	return $scope.currentPatient.significantMutations.tumourNGS.some(function(item) {return item.geneVarientID === geneVarientID});
    } else if(type=='FMTumour'){
    	if(typeof $scope.currentPatient.significantMutations.fmTumour.copyNumberAlteration == 'undefined' ||
    			typeof	$scope.currentPatient.significantMutations.fmTumour.rearrangement == 'undefined' ||
    			typeof $scope.currentPatient.significantMutations.fmTumour.shortVariant == 'undefined') {
    		return false;
    	}
    	if($scope.currentPatient.significantMutations.fmTumour.copyNumberAlteration.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	if($scope.currentPatient.significantMutations.fmTumour.rearrangement.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	if($scope.currentPatient.significantMutations.fmTumour.shortVariant.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	return false;
    } else if(type=='FMBlood'){
    	if(typeof $scope.currentPatient.significantMutations.fmBlood.copyNumberAlteration == 'undefined' ||
    			typeof	$scope.currentPatient.significantMutations.fmBlood.rearrangement == 'undefined' ||
    			typeof $scope.currentPatient.significantMutations.fmBlood.shortVariant == 'undefined') {
    		return false;
    	}
    	if($scope.currentPatient.significantMutations.fmBlood.copyNumberAlteration.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	if($scope.currentPatient.significantMutations.fmBlood.rearrangement.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	if($scope.currentPatient.significantMutations.fmBlood.shortVariant.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	return false;
    } else {
    	if(typeof $scope.currentPatient.significantMutations.genericGenomic =='undefined' || 
    			typeof $scope.currentPatient.significantMutations.genericGenomic[type].copyNumberAlteration == 'undefined' ||
    			typeof	$scope.currentPatient.significantMutations.genericGenomic[type].rearrangement == 'undefined' ||
    			typeof $scope.currentPatient.significantMutations.genericGenomic[type].shortVariant == 'undefined') {
    		return false;
    	}
    	if($scope.currentPatient.significantMutations.genericGenomic[type].copyNumberAlteration.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	if($scope.currentPatient.significantMutations.genericGenomic[type].rearrangement.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	if($scope.currentPatient.significantMutations.genericGenomic[type].shortVariant.some(function(item) {return item.gene_variant_id === geneVarientID})){
    		return true;
    	}
    	return false;
    }
  };

  $scope.toDecimal = function(num, length) {
    var number = Math.round(num * Math.pow(10, length)) / Math.pow(10, length);
    number = number.toFixed(length);

    return number;
  };

  $scope.getLatestMutationSelections = function() {
    // Update the UI with the latest data from the database - sync with selections from other users
    // Get the data
    $http({
      method: 'GET',
      url: 'api/data.jsp?endpoint=getmutationselection&personID='+$scope.currentPatient.person_id,
    }).then(function successCallback(response) {
      // Success: Update the UI
//      console.log(response.data);
      $scope.currentPatient.significantMutations.fmBlood  = response.data.fmBlood;
      $scope.currentPatient.significantMutations.fmTumour = response.data.fmTumour;
      $scope.currentPatient.significantMutations.ctDNA = response.data.ctDNA;
      $scope.currentPatient.significantMutations.tumourNGS = response.data.tumourNGS;
      $scope.currentPatient.significantMutations.genericGenomic = response.data.genericGenomic;
    }, function errorCallback(response) {
      // Error
      console.log('Error: Unable to get the mutation selection.');
    });
  };
  
  $scope.getLatestVersionMutationSelections = function() {  
	if(typeof $scope.currentPatient.person_id === "undefined"){
		return;
	}
    $http({
        method: 'GET',
        url: 'api/data.jsp?endpoint=getmutationselectionlatest&personID='+$scope.currentPatient.person_id,
      }).then(function successCallback(response) {
        // Success: Update the UI
        //console.log(response.data);
        if(response.data.hasOwnProperty("latestTumourNGS")) {
          $scope.currentPatient.significantMutations.latestTumourNGS = response.data.latestTumourNGS;
          $scope.currentPatient.significantMutations.latestCtDNA = response.data.latestCtDNA;
          $scope.currentPatient.significantMutations.latestFmBlood = response.data.latestFmBlood;
          $scope.currentPatient.significantMutations.latestFmTumour = response.data.latestFmTumour;
          $scope.currentPatient.significantMutations.latestGenericGenomic = response.data.latestGenericGenomic;
          $scope.currentPatient.significantMutations.summery = response.data.summery;
          $scope.currentPatient.significantMutations.meetingOutcomeSummarySV = response.data.meetingOutcomeSummarySV;
          $scope.currentPatient.significantMutations.meetingOutcomeSummaryR = response.data.meetingOutcomeSummaryR;
          $scope.currentPatient.significantMutations.meetingOutcomeSummaryCNA = response.data.meetingOutcomeSummaryCNA;
        }
      }, function errorCallback(response) {
        // Error
        console.log('Error: Unable to get the latest version mutation selection.');
      });
  };

  $scope.convertRoleID = function(roleID) {
    var roles = {
      1: "Gatekeeper",
      2: "Editor",
      3: "Reader",
      4: "Data Uploader",
      5: "Deactivated",
      6: "Chief Investigator*"
    };

    return roles[roleID];
  };

  $scope.addUser = function() {
	  if($scope.admin.newUser.email.trim()=='' || $scope.admin.newUser.roleID<1)
		  return;
    $http({
      method: 'POST',
      url: 'api/data.jsp?endpoint=adduser',
      data: $.param({ email: $scope.admin.newUser.email, roleID: $scope.admin.newUser.roleID }),
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).then(function successCallback(response) {
      //// Success
    	// reset all
    	$scope.admin.newUser.roleID=0;
    	$scope.admin.newUser.email='';
      // Show success message
      if(response.data.success == "false") {
        alert("Unable to add new user.");
      }

      // Reload user list
      $http({
        method: 'GET',
        url: 'api/data.jsp?endpoint=listadminusers&random='+Math.random()
      }).then(function successCallback(response) {
        $scope.admin.users = response.data;
//        $scope.$apply();

      }, function errorCallback(response) {
        console.log('Error fetching list of admin users: '+response.status);
      });

    }, function errorCallback(response) {
      // Error
      console.log('Error: Unable to post the new user data.');
    });
  };

  $scope.updateUserDetails = function(whitelistID) {
    $http({
      method: 'POST',
      url: 'api/data.jsp?endpoint=updateuser',
      data: $.param({ whitelistID: whitelistID, roleID: $scope.admin.editUser[whitelistID].roleID }),
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).then(function successCallback(response) {
      if(response.data.success == "false") {
        alert("Unable to update user.");
      }

      // Reload user list
      $http({
        method: 'GET',
        url: 'api/data.jsp?endpoint=listadminusers&random='+Math.random()
      }).then(function successCallback(response) {
        $scope.admin.users = response.data;

      }, function errorCallback(response) {
        console.log('Error fetching list of admin users: '+response.status);
      });

    }, function errorCallback(response) {
      // Error
      console.log('Error: Unable to update the user data.');
    });
  };
  
  $scope.editMeetingOutcome = function(clickEvent, index){
	  //console.log(clickEvent)
	  //console.log(clickEvent.target.parentNode.parentNode.parentNode.id);
	  $http({
		  method: 'GET',
		  url: 'api/data.jsp?endpoint=islocked&personID='+$scope.currentPatient.person_id
	  }).then(function successCallback(response) {
		  if(response.data.success == "true" && response.data.locked =="true") {
			  $scope.showPage('detail', $scope.currentPatient.person_id);
		  } else if(response.data.success == "true" && response.data.locked =="false") {
			  $scope.meeting.edits+=1;
			  if(typeof $scope.meeting.outcome[index].edit === "undefined"){
				  $scope.meeting.outcome[index].edit=angular.copy($scope.meeting.outcome[index]);
				  var meetingOutcomeString = $scope.meeting.outcome[index].edit.outcome;
				  if(typeof meetingOutcomeString != "undefined"){
					  $scope.meeting.outcome[index].edit.outcome = meetingOutcomeString.split("\n");
				  }
		  	  }
			  console.log(typeof $scope.meeting.outcome[index].edit.outcome);
			  $scope[clickEvent.target.parentNode.parentNode.parentNode.id+'edit']=true;
		  }
    });
  };
  
  $scope.saveEditMeetingOutcome = function(clickEvent, index){
	  
	  var type='';
	  var value=''
	  
	  if(clickEvent.target.parentNode.parentNode.id.startsWith("outcome")){
		  type='outcome';
		  if(Array.isArray($scope.meeting.outcome[index].edit.outcome)) {
		      var meetingOutcomeArray = $scope.meeting.add.outcome;
		      value = $scope.meeting.outcome[index].edit.outcome.join("\n");
		  } else {
			  value=$scope.meeting.outcome[index].edit.outcome;
		  }
	  } else if(clickEvent.target.parentNode.parentNode.id.startsWith("notes")){
		  type='notes';
		  value=$scope.meeting.outcome[index].edit.notes;
	  }
	  
	    // Send to API
	    $http({
	      method: 'POST',
	      url: 'api/data.jsp?endpoint=postmeetingoutcomeedit',
	      data: $.param({ meetingId: $scope.meeting.outcome[index].edit.meeting_id, type: type, value: value}),
	      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
	    }).then(function successCallback(response) {
	      //console.log('Meeting outcome updated.');

	      // Show success message
	      $scope.meeting.message = 'Meeting outcome updated.';
	      if(clickEvent.target.parentNode.parentNode.id.startsWith("outcome")){
	    	  $scope.meeting.outcome[index].outcome=$scope.meeting.outcome[index].edit.outcome.join("\n");
	    	  if($scope['notes'+index+'edit']==false){
	    		  delete $scope.meeting.outcome[index].edit;
	    	  }
	      } else if(clickEvent.target.parentNode.parentNode.id.startsWith("notes")){
	    	  $scope.meeting.outcome[index].notes=$scope.meeting.outcome[index].edit.notes;
	    	  if($scope['outcome'+index+'edit']==false){
	    		  delete $scope.meeting.outcome[index].edit;
	    	  }
	      }
	      $scope.meeting.edits-=1;
	      $scope.meeting.outcome[index].lastUpdated=response.data.lastUpdated;
	      $timeout( function() {
	        $scope.meeting.message = '';
	      }, 10000);
	    }, function errorCallback(response) {
	      //console.log('Error fetching meeting outcome: '+response.status);

	      $scope.meeting.message = '';
	      $scope.showPage('detail', $scope.currentPatient.person_id);
	      // Show error message
	      if(typeof response.data.error!= "undefined"){
	    	  $scope.currentPatient.editLockMessage = response.data.error;
	      } else {
	    	  alert('Error: Unable to update the meeting outcome.');
	      }
	    });
	  
	  
	  $scope[clickEvent.target.parentNode.parentNode.id+'edit']=false;
  };
  
  $scope.cancelEditMeetingOutcome = function(clickEvent, index){
	  //console.log(clickEvent)
	  //console.log(clickEvent.target.parentNode.parentNode.id);
	  //console.log(index);
	  $scope[clickEvent.target.parentNode.parentNode.id+'edit']=false;
	  delete $scope.meeting.outcome[index].edit;
	  $scope.meeting.edits-=1;
  };
  
  $scope.showedit = function(variable) {
	  if ( typeof variable === 'undefined' ) return false;
	  else return variable;
  };
  
  $scope.loadComponent = function(component, source) {
	  if(typeof source === 'undefined'){
		  if( typeof $scope.currentPatient.person_id === 'undefined') return "";
		  return 'rest/component/'+component+'/'+$scope.currentPatient.person_id+'?rnd='+$scope.timestamp;
	  } else {
		  if( typeof $scope.currentPatient.person_id === 'undefined') return "";
		  return 'rest/component/'+component+'/'+source+'/'+$scope.currentPatient.person_id+'?rnd='+$scope.timestamp;
	  }
  }
  
  $scope.unlockEdit = function() {
	  $http({
	      method: 'GET',
	      url: 'api/data.jsp?endpoint=unlockedit'
	    });
	  
  }
    
  $scope.checkLockStatus = function() {
	  console.log("checkLockStatus called");
	  $http({
		  method: 'GET',
		  url: 'api/data.jsp?endpoint=islocked&personID='+$scope.currentPatient.person_id
	  }).then(function successCallback(response) {
		  if(response.data.success == "true" && response.data.locked =="false") {
			  $scope.showPage('detail', $scope.currentPatient.person_id);
		  } 
      });
  }
  
  $scope.definePatientOrder = function(column){
	  if(column==$scope.patientView.orderByField){
		  $scope.patientView.order=($scope.patientView.order+1)%3;
		  if($scope.patientView.order==0) $scope.patientView.orderByField='none';
	  } else {
		  $scope.patientView.order=1;
		  $scope.patientView.orderByField=column;
	  }
  }
  
  $scope.defineMOMutationsOrder = function(column){
	  if(column=='timepoint'){
		  column=['timepoint','gene'];
	  }
	  if(column==$scope.MOView.orderByField || angular.equals(column, $scope.MOView.orderByField)){
		  $scope.MOView.order=($scope.MOView.order+1)%3;
		  if($scope.MOView.order==0) {
			  $scope.MOView.orderByField=['source','gene'];
			  $scope.MOView.order=0;
		  }
	  } else {
		  $scope.MOView.order=1;
		  $scope.MOView.orderByField=column;
	  }
  }
  
  $scope.compareFunc = function(v1, v2) {
	    // If we don't get strings, just compare by index
	    if (v1.type !== 'string' || v2.type !== 'string') {
	      return (v1.index < v2.index) ? -1 : 1;
	    }

	    // Compare strings alphabetically, taking locale into account
	    if($scope.patientView.orderByField==='consentDate' || $scope.patientView.orderByField==='mtbDate'){
	    	var parts = v1.value.split("/");
			  if(parts.length==3){
				  v1.value=parts[2]+parts[1]+parts[0];
			  }
			  parts = v2.value.split("/");
			  if(parts.length==3){
				  v2.value=parts[2]+parts[1]+parts[0];
			  }
	    }
	    
	    return v1.value.localeCompare(v2.value);
	  };
	 
	  
  $scope.newlySelected = function(element) {
	  if(typeof element != "undefined"){
		  return element.newlySelected === true;
	  } else return false;
  }
  
  $scope.lastBaseline = function(index,part) {
	  if(part=='NGS'){
		  var objs = Object.keys($scope.currentPatient.ngsSubset.baseline);
		  if(index+1==objs.length) {
			  return true;
		  };
		  var ret;
		  for(i=index+1;i<objs.length;i++){
			 ret=$scope.findVal($scope.currentPatient.ngsSubset.baseline[objs[i]],'geneSubset');
			 if(typeof ret!=='undefined'){
				  return false;
			 }
			  
		  };
		  return(true);
	  }
	  if(part=='Exploartory'){
		  var objs = Object.keys($scope.currentPatient.exploratory.baseline);
		  if(index+1==objs.length) {
			  return true;
		  };
		  var ret;
		  for(i=index+1;i<objs.length;i++){
			 ret=$scope.findVal($scope.currentPatient.exploratory.baseline[objs[i]],'geneSubset');
			 if(typeof ret!=='undefined'){
				  return false;
			 }
			  
		  };
		  return true;
	  }
	  if(part=='Tumour'){
		  var objs = Object.keys($scope.currentPatient.tumourNGS.baseline);
		  if(index+1==objs.length) {
			  return true;
		  };
		  var ret;
		  for(i=index+1;i<objs.length;i++){
			 ret=$scope.findVal($scope.currentPatient.tumourNGS.baseline[objs[i]],'geneSubset');
			 if(typeof ret!=='undefined'){
				  return false;
			 }
			  
		  };
		  return true;
	  }
	  if(part=='FMTumour'){
		  var objs = Object.keys($scope.currentPatient.fmTumour);
		  if(index+1==objs.length) {
			  return true;
		  };
		  var ret;
		  for(i=index+1;i<objs.length;i++){
			 ret=$scope.findVal($scope.currentPatient.fmTumour[objs[i]],'tmb_status');
			 if(typeof ret!=='undefined'){
				  return false;
			 }
			  
		  };
		  return true;
	  }
	  if(part=='FMBlood'){
		  var objs = Object.keys($scope.currentPatient.fmBlood.baseline);
		  if(index+1==objs.length) {
			  return true;
		  };
		  var ret;
		  for(i=index+1;i<objs.length;i++){
			 ret=$scope.findVal($scope.currentPatient.fmBlood.baseline[objs[i]],'tmb_status');
			 if(typeof ret!=='undefined'){
				  return false;
			 }
			  
		  };
		  return true;
	  }
  }
  
  $scope.findVal = function(object, key) {
	    var value;
	    if(object == 'undefined') return false;
	    Object.keys(object).some(function(k) {
	        if (k === key) {
	            value = object[k];
	            return true;
	        }
	        if (object[k] && typeof object[k] === 'object') {
	            value = $scope.findVal(object[k], key);
	            return value !== undefined;
	        }
	    });
	    return value;
	}
  
  $scope.loadGeneralInfo = function(personID) {
      $http({
          method: 'GET',
          url: 'api/data.jsp?endpoint=generalinfo&personID='+personID
        }).then(function successCallback(response) {
          //console.log('Fetched treatment details.');
          ////console.log(response.data);

          // Update the model
          $scope.currentPatient.treatments = response.data;

        }, function errorCallback(response) {
          //console.log('Error fetching treatment details: '+response.status);
        });
  }
  
  $scope.loadTumour = function(personID) {
	  if($scope.tumourngs=='false') return;
      $http({
          method: 'GET',
          url: 'api/data.jsp?endpoint=tumourngs&personID='+personID
        }).then(function successCallback(response) {

          // Update the model
          if(typeof response.data.baseline != "undefined") {
          	
          	$scope.currentPatient.tumourNGS.baseline=response.data.baseline;

              // Check for a specimen date
              if(typeof response.data.baseline['1'] == "undefined" || 
            		  typeof response.data.baseline['1'].specimen.specimenDate == "undefined") {
                // No specimen date
                $scope.detailSection.tumourSubs[1] = false;
              }
              
              if(typeof response.data.baseline!= "undefined" && 
              		typeof response.data.baseline['1'] != "undefined" &&
              		typeof response.data.baseline['1'].runs['1'] != "undefined" && 
              		typeof response.data.baseline['1'].runs['1']['1'] != "undefined" &&
              		typeof response.data.baseline['1'].runs['1']['1'].geneSubset != "undefined" && 
              		response.data.baseline['1'].runs['1']['1'].geneSubset.length >0){
              	$scope.detailSection.tumourSubs[1] = true;
              }
              
              if(typeof response.data.baseline!= "undefined" && typeof response.data.baseline['1'] != "undefined" && typeof response.data.baseline['1'].runs['1'] != "undefined"){
                  Object.keys($scope.currentPatient.tumourNGS.baseline).forEach(function(baseline, i) {
                      $scope['active_tumour'][baseline]={};
      				Object.keys($scope.currentPatient.tumourNGS.baseline[baseline].runs).forEach(function(key,index) {
      				    // key: the name of the object key
      				    // index: the ordinal position of the key within the object 
      				    
      				    $scope['active_tumour'][baseline][key]=''+Object.keys($scope.currentPatient.tumourNGS.baseline[baseline].runs[key]).length;
      				});
      			});
              }

              } else {
                // Results don't exist
                $scope.detailSection.tumourSubs[1] = false;
              }

        }, function errorCallback(response) {
          //console.log('Error fetching tumour NGS details: '+response.status);
        });

  }
  
  $scope.loadFMTumour = function(personID){
	  if($scope.fmtumour=='false') return;
	  $http({
		  method: 'GET',
		  url: 'rest/FMTumour/'+personID
	  }).then(function successCallback(response){
		  $scope.currentPatient.fmTumour = response.data;
		  $scope.showTumourVUS=false;
		  
		  if(typeof $scope.currentPatient.fmTumour['1']!= "undefined"){
			  //$scope.active_FMTumour={1:{}};
              Object.keys($scope.currentPatient.fmTumour).forEach(function(baseline, i) {
  				 $scope.active_FMTumour[1][baseline]=''+(Object.keys($scope.currentPatient.fmTumour[baseline]).length-1);
  			});
          }

		  
	  }, function errorCallback(response) {
		  $scope.currentPatient.fmTumour = {};
		  $scope.active_FMTumour={1:{}};
      });
  }
  
  $scope.loadFMBlood = function(personID){
	  if($scope.fmblood=='false') return;
	  $http({
		  method: 'GET',
		  url: 'rest/FMBlood/'+personID
	  }).then(function successCallback(response){
		  $scope.currentPatient.fmBlood={};
		  $scope.currentPatient.fmBlood.baseline = response.data;
		  $scope.showBloodVUS=false;
		  
		  if(typeof $scope.currentPatient.fmBlood.baseline['1']!= "undefined"){
              Object.keys($scope.currentPatient.fmBlood.baseline).forEach(function(baseline, i) {
  				 $scope.active_FMBlood[1][baseline]=''+(Object.keys($scope.currentPatient.fmBlood.baseline[baseline]).length-1);
  			   });
              $scope.currentPatient.fmBlood.baselineList=$scope.getArray($scope.currentPatient.fmBlood.baseline);
              
          } else {
        	  $scope.currentPatient.fmBlood.baselineList=[];
          }
		  
	  }, function errorCallback(response) {
		  $scope.currentPatient.fmBlood = {};
		  $scope.active_FMBlood={1:{}};
      });
  }
  
  $scope.loadGenomicsData = function(personID){
	  //get list of configured sources
	  generic_li=angular.element('.generic-genomics-data');
	  for(i=0;i<generic_li.length;i++){
		  name=generic_li[i].id;
		  if(name.endsWith('blood')){
			  type='blood';
		  }
		  else {
			  type='tumour';
		  }
		  if(name.startsWith("detail-sidebar-")){
			  let source_short=name.slice(15, name.lastIndexOf("_"));
			  let source=name.slice(15);
			  let tabname='active_'+source;
			  $scope[tabname]={1:{}};
			  //get data
			  $http({
				  method: 'GET',
				  url: 'rest/GenomicsData/'+source_short+'/'+personID+'/'+type
			  }).then(function successCallback(response){
				  $scope.currentPatient[source]={baseline:{}};
				  $scope.currentPatient[source].baseline = response.data;
				  $scope.showBloodVUS=false;
				  
				  if(typeof $scope.currentPatient[source].baseline['1']!= "undefined"){
		              Object.keys($scope.currentPatient[source].baseline).forEach(function(baseline, i) {
		  				 $scope[tabname][1][baseline]=''+(Object.keys($scope.currentPatient[source].baseline[baseline]).length-1);
		  			   });
		              $scope.currentPatient[source].baselineList=$scope.getArray($scope.currentPatient[source].baseline);
		              
		              if($scope[tabname+'Watcher']){
		            	  $scope[tabname+'Watcher']();
		              }
		              $scope[tabname+'Watcher']=$scope.$watch(tabname, function(newValue, oldValue){
		            	  if(typeof $scope.currentPatient[source] != 'undefined' && typeof $scope.currentPatient[source].baseline != 'undefined'){
		            		  $timeout.cancel($scope[source+'timeout']);
		            		  $scope[source+'timeout']=$timeout(function() {
		                        	if(typeof $scope[tabname][1][1]!='undefined' && $scope[tabname][1][1]==null){
		            	            	Object.keys($scope.currentPatient[source].baseline).forEach(function(baseline, i) {
		            	            		if($scope[tabname][1][baseline]==null){
		            	     				  $scope[tabname][1][baseline]=''+(Object.keys($scope.currentPatient[source].baseline[baseline]).length-1);
		            	            		}
		            	     			});
		            	            }
		            			}, 500);
		            	  }
		              }, true); 
		              
		          } else {
		        	  $scope.currentPatient[source].baselineList=[];
		          }
				  
			  }, function errorCallback(response) {
				  $scope.currentPatient[source] = {};
				  $scope[tabname]={1:{}};
		      });
			  $scope.showBaseline(source, 1); 
			 
		  }
	  }
  } 
  
  $scope.loadNGSSubset = function(personID){
	  if($scope.ctdnangs=='false') return;
      $http({
          method: 'GET',
          url: 'api/data.jsp?endpoint=ctdnangssubset&personID='+personID
        }).then(function successCallback(response) {
          // Clear previous patient's results
          $scope.currentPatient.ngsSubset.baseline =  {};
          
          if(typeof response.data.listGenes != "undefined"){
              var listGenes = response.data.listGenes;
              $scope.currentPatient.ngsSubset.listGenes = listGenes.join(', ');
          }
                  
          // Check there are specimens for the current patient
          if(typeof response.data.baseline['1'] != "undefined" && response.data.baseline['1'].specimen.specimenDate.length != 0) {
            // Check there are results for the current patient
            // sample taken but not analysed
            if(typeof response.data.baseline['1'].runs['1'] != "undefined" && typeof response.data.baseline['1'].runs['1']['1'] != "undefined" && 
            		response.data.baseline['1'].runs['1']['1'].hasOwnProperty('geneSubset')) {
              // Update the results
              $scope.detailSection.ngsSubset.noResults.message = '';
              $scope.currentPatient.ngsSubset.baseline = response.data.baseline;
              $scope.currentPatient.ngsSubset.baselineList = $scope.getArray($scope.currentPatient.ngsSubset.baseline);
            } else {
              $scope.detailSection.ngsSubset.noResults.message = 'Results not available yet.';
              $scope.detailSection.ngsSubset.noResults.specimenDate = response.data.baseline['1'].specimen.specimenDate;
              $scope.currentPatient.ngsSubset.baselineList = [];
            }

            $scope.currentPatient.ngsSubset.specimenDate = response.data.baseline['1'].specimen.specimenDate;
          } else if (typeof response.data.baseline['1'] != "undefined" && typeof response.data.baseline['1'].runs['1']['1'] == "undefined"){
            $scope.detailSection.ngsSubset.noResults.message = 'Sample not taken.';
            $scope.detailSection.ngsSubset.noResults.specimenDate = '';
            $scope.currentPatient.ngsSubset.baseline = {};

            // Hide the tables
            $scope.currentPatient.ngsSubset.ngsLib = {};
            $scope.currentPatient.ngsSubset.baselineList = [];
          } else {
            $scope.detailSection.ngsSubset.noResults.message = 'No blood samples found.';
            $scope.detailSection.ngsSubset.noResults.specimenDate = '';
            $scope.currentPatient.ngsSubset.baseline = {};
            $scope.currentPatient.ngsSubset.baselineList = [];

            // Hide the tables
            $scope.currentPatient.ngsSubset.ngsLib = {};
          }
          
          // Nil result, analysisFailed
          if(typeof response.data.baseline['1'] != "undefined" && typeof response.data.baseline['1'].runs['1']!= "undefined" && 
        		typeof response.data.baseline['1'].runs['1']['1'] != "undefined" && (response.data.baseline['1'].runs['1']['1'].showNilResult == 'true' 
        		|| response.data.baseline['1'].runs['1']['1'].noMutationsFound == 'true' || response.data.baseline['1'].runs['1']['1'].analysisFailed == 'true')) {
            $scope.currentPatient.ngsSubset.baseline = response.data.baseline;
            $scope.detailSection.ngsSubset.noResults.message = '';
          }
          if(typeof response.data.baseline['1'] != "undefined" && typeof response.data.baseline['1'].runs['1'] != "undefined"){
              Object.keys($scope.currentPatient.ngsSubset.baseline).forEach(function(baseline, i) {
                  $scope['active_ctdna'][baseline]={};
  				Object.keys($scope.currentPatient.ngsSubset.baseline[baseline].runs).forEach(function(key,index) {
  				    // key: the name of the object key
  				    // index: the ordinal position of the key within the object 
  				    
  				    $scope['active_ctdna'][baseline][key]=''+Object.keys($scope.currentPatient.ngsSubset.baseline[baseline].runs[key]).length;
  				});
  			});
          }
          for(var bl in $scope.currentPatient.ngsSubset.baseline){
          	if($scope.currentPatient.ngsSubset.baseline[bl].hasOwnProperty('runs') &&
          			(!$scope.currentPatient.ngsSubset.baseline[bl].runs.hasOwnProperty('1') || 
          			  $scope.currentPatient.ngsSubset.baseline[bl].runs['1']['1'].hasOwnProperty('geneSubset') == false)){
          		$scope.currentPatient.ngsSubset.baseline[bl].message='Results not available yet.';
          	} else {
          		$scope.currentPatient.ngsSubset.baseline[bl].message='';
          	}
          }
        }, function errorCallback(response) {
          //console.log('Error fetching ngs subset details: '+response.status);
        });

  }
  
  $scope.loadNGSExploartory = function(personID){
	  if($scope.ctdnaexploratory=='false') return;
      $http({
          method: 'GET',
          url: 'api/data.jsp?endpoint=ctdnaexploratory&personID='+personID
        }).then(function successCallback(response) {
          // Clear previous patient's results
          $scope.currentPatient.exploratory.baseline =  {};

          // Check there are specimens for the current patient
          if(typeof response.data.baseline['1'] != "undefined" && typeof response.data.baseline['1'].specimen.specimenDate.length != "undefined") {
            // Check there are results for the current patient
            if(typeof response.data.baseline['1'].runs['1'] != "undefined" && typeof response.data.baseline['1'].runs['1']['1'] != "undefined" && ((response.data.baseline['1'].runs['1']['1'].hasOwnProperty('geneSubset') && response.data.baseline['1'].runs['1']['1'].geneSubset.length > 0) || response.data.baseline['1'].runs['1']['1'].showNilResult == "true")) {
              $scope.detailSection.exploratory.noResults.message = '';
              $scope.currentPatient.exploratory.baseline = response.data.baseline;
              $scope.currentPatient.exploratory.baselineList = $scope.getArray($scope.currentPatient.exploratory.baseline);

              // Show the tables
//              $scope.detailSection.ctdnaExploratorySubs.ctdnaData = true;
//              $scope.detailSection.ctdnaExploratorySubs.ngsLib = true;
            } else {
              $scope.detailSection.exploratory.noResults.message = 'Results not available yet.';
              $scope.detailSection.exploratory.noResults.specimenDate = response.data.baseline['1'].specimen.specimenDate;
              $scope.currentPatient.exploratory.baselineList = [];
              //console.log('set specimen Date ' + $scope.detailSection.exploratory.noResults.specimenDate);

              // Hide the tables
//              $scope.detailSection.ctdnaExploratorySubs.ctdnaData = false;
//              $scope.detailSection.ctdnaExploratorySubs.ngsLib = false;
            }

          } else if (typeof response.data.baseline['1'] != "undefined" && typeof response.data.baseline['1'].runs['1']['1'] == "undefined") {
            $scope.detailSection.exploratory.noResults.message = 'Sample not taken.';
            $scope.detailSection.exploratory.noResults.specimenDate = '';
            $scope.currentPatient.exploratory.baseline = {};
            $scope.currentPatient.exploratory.listGenes = '';
          } else {
            $scope.detailSection.exploratory.noResults.message = 'No blood samples found.';
            $scope.detailSection.exploratory.noResults.specimenDate = '';
            $scope.currentPatient.exploratory.baseline = {};
            $scope.currentPatient.exploratory.listGenes = '';
          }
          
          // Nil result or analysisFailed
          if(typeof response.data.baseline['1'] != "undefined" && (typeof response.data.baseline['1'].runs['1']=='undefined' ||
        		  typeof response.data.baseline['1'].runs['1']['1']== 'undefined')){
          
          } else if(typeof response.data.baseline['1'] != "undefined" && (response.data.baseline['1'].runs['1']['1'].showNilResult == 'true' || response.data.baseline['1'].runs['1']['1'].analysisFailed== 'true' || response.data.baseline['1'].runs['1']['1'].noMutationsFound== 'true')) {
            $scope.currentPatient.exploratory.baseline = response.data.baseline;
            $scope.detailSection.exploratory.noResults.message = '';
          }
          
          if(typeof response.data.baseline['1'] != "undefined" && typeof response.data.baseline['1'].runs['1'] != "undefined"){
              Object.keys($scope.currentPatient.exploratory.baseline).forEach(function(baseline, i) {
                  $scope['active_exploratory'][baseline]={};
  				Object.keys($scope.currentPatient.exploratory.baseline[baseline].runs).forEach(function(key,index) {
  				    // key: the name of the object key
  				    // index: the ordinal position of the key within the object 
  				    $scope['active_exploratory'][baseline][key]=''+Object.keys($scope.currentPatient.exploratory.baseline[baseline].runs[key]).length;
  				});
  			});
          }
          
          for(var bl in $scope.currentPatient.exploratory.baseline){
          	if($scope.currentPatient.exploratory.baseline[bl].hasOwnProperty('runs') && 
          			(!$scope.currentPatient.exploratory.baseline[bl].runs.hasOwnProperty('1') || 
          			$scope.currentPatient.exploratory.baseline[bl].runs['1']['1'].hasOwnProperty('geneSubset') == false)){
          		$scope.currentPatient.exploratory.baseline[bl].message='Results not available yet.';
          	} else {
          		$scope.currentPatient.exploratory.baseline[bl].message='';
          	}
          }

        }, function errorCallback(response) {
          //console.log('Error fetching exploratory details: '+response.status);
        });

  }
  
  $scope.loadIHCReport = function(personID){
	  if($scope.ihc=='false') return;
	  $http({
		  method: 'GET',
		  url: 'rest/IHCReport/'+personID
	  }).then(function successCallback(response){
		  $scope.currentPatient.ihc=response.data;
		  $scope.showIHCBaseline(1);
		  for(var timepoint in $scope.currentPatient.ihc){
			  if($scope.currentPatient.ihc[timepoint][1].hasOwnProperty('reportDate')){
				  $scope.showIHCBaseline(timepoint);
				  break;
			  }
		  }
		  Object.keys($scope.currentPatient.ihc).forEach(function(baseline, i) {
		      $scope.active_ihc[1][baseline]=''+(Object.keys($scope.currentPatient.ihc[baseline]).length);
		  });
		  
		  
	  }, function errorCallback(response) {
		  $scope.currentPatient.ihc = {};
		  $scope.active_ihc={1:{}};
      });
  }
  
  $scope.deleteReport = function(specimen_id, run) {
	  var deleteRep = confirm('Please confirm the deletion of this report!');
      if(deleteRep) {
	    $http({
	      method: 'DELETE',
	      url: 'rest/report/'+specimen_id+'/'+run
	    }).then(function successCallback(response) {
	    	console.log('Delete OK')
	    	//reload data
	    	ret=$scope.loadGeneralInfo($scope.currentPatient.person_id);
	    	ret=$scope.loadTumour($scope.currentPatient.person_id);
	    	ret=$scope.loadNGSSubset($scope.currentPatient.person_id);
	    	ret=$scope.loadNGSExploartory($scope.currentPatient.person_id);
	    }, function errorCallback(response) {
	        console.log('Error Delete '+response+' '+response.status);
	    });
      }
   };
   
   $scope.getAllGenes = function() {
	   $http({
		   method: 'GET',
		   url: 'api/data.jsp?endpoint=allgenes'
	   }).then(function successCallback(response) {
		   $scope.allGenes=response.data;
	   }, function errorCallback(response) {
		   console.log('Error getting genes ' + response + ' ' + response.status);
		   $scope.allGenes=[];
	   });
   }
   
   $scope.getAllGenesPanel = function() {
	   $http({
		   method: 'GET',
		   url: 'api/data.jsp?endpoint=allgenespanel'
	   }).then(function successCallback(response) {
		   $scope.allGenesPanel=response.data;
		   $scope.allPanelNames=Object.keys($scope.allGenesPanel);
	   }, function errorCallback(response) {
		   console.log('Error getting genes ' + response + ' ' + response.status);
		   $scope.allGenesPanel=[];
	   });
   }
   
   $scope.addGeneToPanel = function(gene) {
	   if(typeof gene === 'string' ){
		   if(gene.trim()==''){
			   return;
		   }
		   //add new gene
		   add_gene ={};
		   add_gene.gene_concept_id=-1;
		   add_gene.gene_name=gene.toUpperCase();
	   } else {
		   add_gene=gene;
	   }
	   if( $scope.genePanel.geneList.map(function(e) { return e.gene_name; }).indexOf(add_gene.gene_name)<=-1){
		   $scope.genePanel.geneList.push(add_gene);
	   }
	   $scope.genePanel.geneModel='';
   }
   
   $scope.removeGeneFromPanel =  function(gene) {
	   $scope.genePanel.geneList.splice($scope.genePanel.geneList.indexOf(gene),1);
   }
   
   $scope.saveGenePanel = function() {
	   console.log('save Gene Panel called');
	   // do something about it// Send to API
	    $http({
	        method: 'POST',
	        url: 'api/data.jsp?endpoint=postgenepanel',
	        data: $.param({ personID: $scope.currentPatient.person_id, 
	        				date: $scope.genePanel.date,
	        	            genePanelName: $scope.genePanel.name,
	        	            genePanelGenes: angular.toJson($scope.genePanel.geneList)}),
	        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
	      }).then(function successCallback(response) {
			   $scope.genePanel.name='';
			   $scope.genePanel.geneList=[];
			   $scope.genePanel.geneModel='';
			   $scope.genePanel.date='';
			   $scope.genePanel.basePanel='';
			   $scope.genePanel.message = 'New gene panel saved.';
			   $timeout( function() {
			        $scope.genePanel.message = '';
			   }, 10000);
	      }, function errorCallback(response) {
	    	  console.log(response)
	    	  alert('Error: Unable to save new gene panel.');
	      });
   }
   
   $scope.loadBasePanel = function() {
	   $scope.genePanel.geneList=[];
	   if($scope.genePanel.basePanel>-1){
		   $scope.allGenesPanel[$scope.allPanelNames[$scope.genePanel.basePanel]].genes.forEach(function(element) {
			   $scope.addGeneToPanel(element);
		   });
		   //for(var gene in $scope.allGenesPanel[$scope.allPanelNames[$scope.genePanel.basePanel]]){
			//   $scope.addGeneToPanel(gene);
		   //}
	   }
   }
   
   $scope.checkNewlySelected = function(summery){
	   if(summery.newlySelcted != "undefined"){
		   return summery.newlySelected==='true';
	   }
	   return false;
   }
   
   
   $scope.getArray = function(bloods){
	   var l=[];
	   Object.keys(bloods).forEach(function(baseline) {
		   var parts = bloods[baseline].specimen.specimenDate.split("/");
		   var d1 = new Date(Number(parts[2]), Number(parts[1]) - 1, Number(parts[0])).getTime();
		   var i={baseline:baseline,
					  date:d1 }
		   l.push(i);
	   });
	   return l;
   }
   
   $scope.navigateNext = function(){
	   var orderPatients=$filter('orderBy')($scope.allPatients, 'targetID')
	   
	   var index = -1;

	   orderPatients.some(function(obj, i) {
	     return obj.targetID === $scope.currentPatient.id ? index = i : false;
	   });
	   
	   if(index!=-1 && index<orderPatients.length) {
		   $scope.resetCurrentPatient();
		   $scope.showPage('detail', orderPatients[index+1].personID);
	   }
	   
	   
   }
   
   $scope.navigatePrev = function(){
	   var orderPatients=$filter('orderBy')($scope.allPatients, 'targetID')
	   
	   var index = -1;

	   orderPatients.some(function(obj, i) {
	     return obj.targetID === $scope.currentPatient.id ? index = i : false;
	   });
	   
	   if(index!=-1 && index>0) {
		   $scope.resetCurrentPatient();
		   $scope.showPage('detail', orderPatients[index-1].personID);
	   }
   }
   
   $scope.first = function(){
	   var orderPatients=$filter('orderBy')($scope.allPatients, 'targetID')
	   
	   var index = -1;
	   if(typeof orderPatients != 'undefined'){
		   orderPatients.some(function(obj, i) {
		     return obj.targetID === $scope.currentPatient.id ? index = i : false;
		   }); 
		   if(index==0) return true;
	   }
	   return false;
   }
   
   $scope.last = function(){
	   var orderPatients=$filter('orderBy')($scope.allPatients, 'targetID')
	   
	   var index = -1;

	   if(typeof orderPatients != 'undefined'){
		   orderPatients.some(function(obj, i) {
		     return obj.targetID === $scope.currentPatient.id ? index = i : false;
		   });
		   if(index==orderPatients.length-1) return true;
	   }
	   return false;
   }
   
   $scope.arrFromAllGenesPanel = function() {
	  return Object.keys($scope.allGenesPanel).map(function(key) {
	   return $scope.allGenesPanel[key];
	  });
   }
   
   $scope.resetCurrentPatient = function() {
	   $scope.currentPatient = {
			    id: '',
			    person_id: '',
			    disease: '',
			    age: '',
			    gender: '',
			    site: '',
			    dateDiagnosis: '',
			    dateConsent: '',
			    treatments: {},
			    samples: {
			      1: {dateBlood: '', dateTumour: '', tumourType: '', tumourNature: '', pdxCDX: '', dateGDL: ''}
			    },
			    tumourNGS: {
			      baseline: {}
			    },
			    reportGDL: {},
			    reportCEP: {},
			    ngsSubset: {
			      listGenes: {},
			      baseline: {},
			      ngsLib: {}
			    },
			    exploratory: {
			      baseline: {},
			      ngsLib: {}
			    },
			    fmBlood: {
			    	baseline: {}
			    },
			    significantMutations: {
			      tumourNGS: [],
			      ctDNA: [],
			      fmTumour: {
			    	  copyNumberAlteration: [],
			    	  rearrangement: [],
			    	  shortVariant: []
			      },
			      fmBlood: {
			    	  copyNumberAlteration: [],
			    	  rearrangement: [],
			    	  shortVariant: []
			      },
			      genericGenomic: {},
			      latestTumorNGS:[],
			      latestCtDNA: [],
			      meetingOutcomeSummaryCNA: {},
			      meetingOutcomeSummaryR: {},
			      meetingOutcomeSummarySV: {},
			      latestFmTumour: {
			    	  copyNumberAlteration: [],
			    	  rearrangement: [],
			    	  shortVariant: []
			      },
			      latestFmBlood: {
			    	  copyNumberAlteration: [],
			    	  rearrangement: [],
			    	  shortVariant: []
			      },
			      latestGenericGenomic: {},
			      summery:[],
			      ihc: {}
			    }
			  };
	   
	   generic_li=angular.element('.generic-genomics-data');
	   for(i=0;i<generic_li.length;i++){
	 	  name=generic_li[i].id;
	 	  if(name.startsWith("detail-sidebar-")){
	 		  source=name.slice(15);
	 	  }
	 	  
	 	  $scope.currentPatient.significantMutations.genericGenomic[source]= {
	 	    	  copyNumberAlteration: [],
	 	    	  rearrangement: [],
	 	    	  shortVariant: []
	 	      }
	 	 $scope.currentPatient.significantMutations.latestGenericGenomic[source]= {
	 	    	  copyNumberAlteration: [],
	 	    	  rearrangement: [],
	 	    	  shortVariant: []
	 	      }
	   }

   }
   
   $scope.orderByDateReverse = function(item) {
	   if(item!=null) {
		   var parts = item.startDate.split('/');
		   var number = parseInt(parts[2] + parts[1] + parts[0]);

		   return -number;
	   }
	};
	
	$scope.deletePatient = function(personID) {
		if(confirm("The patient and their data will be permanently deleted from eTARGET.  Do you want to continue?")) {
			 $http({
			        method: 'DELETE',
			        url: 'rest/person/'+personID,
			      }).then(function successCallback(response) {
			    	  $scope.allPatients=$scope.allPatients.filter(function(person) {if(person.personID!=personID) {return person;}})
			      }, function errorCallback(response) {
			    	  console.log(response)
			    	  alert('Error: Unable to delete patient.');
			      });
	    } 
	}
	
	$scope.containsChiefInvestigator = function() {
		found=false;
		if(typeof $scope.admin.users != 'undefined'){
			Object.keys($scope.admin.users).forEach(function(user){
		        if ($scope.admin.users[user].roleID === "6") {
		            found=true;
		        }
		    });
		}
		return found;
	}
	
	$scope.removePDX = function() {
		document.querySelector( '#pdx_menu' ).remove();
	}
  
	$scope.getGenericGenomicMOPart = function(meeting, indicator) {
		if(meeting==null || !meeting.hasOwnProperty("genericGenomic")){
			return [];
		}
		var sum=[];
		var firstLevelKeys = Object.keys(meeting.genericGenomic);
		for(i=0;i<firstLevelKeys.length;i++){
//			console.log(firstLevelKeys[i]);
			var ggKeys = Object.keys(meeting.genericGenomic[firstLevelKeys[i]]);
            if(ggKeys!=null){
				var ggKeysFilter= ggKeys.filter(
						function(word) {
							return word.endsWith(indicator);
						}
					);
				for(j=0;j<ggKeysFilter.length;j++){
					sum.push.apply(sum,meeting.genericGenomic[firstLevelKeys[i]][ggKeysFilter[j]]);
				}
            }
		}
		return sum;
	}
	
	$scope.getTrialFinderArguments = function() {
		args="cancerType="+$scope.currentPatient.disease;
		mutations="&alterations=";
		for(i in $scope.currentPatient.significantMutations.summery){
			alteration=$scope.currentPatient.significantMutations.summery[i];
			if(alteration.type=='Short Variant'){
				mutations+=alteration.gene + " SV " + alteration.description + ",";
			}
			if(alteration.type=="Copy Number Alteration") {
				mutations +=alteration.gene + " CNA "+alteration.description.replace(/ /g, "_")+",";
			}
			if(alteration.type=='Rearrangement') {
				genes=alteration.gene.split(" - ")
				mutations += genes[0] + " rearrangement "+alteration.rearrType+",";
				if(genes.length>1){
					mutations += genes[1] + " rearrangement "+alteration.rearrType+",";
				}
			}
		}
		if(mutations.length>13){
			args+=mutations.slice(0,-1);
		}
		return encodeURI(args);
	}
	
	$scope.setConfig = function(ihc,tumourngs,fmblood,fmtumour,ctdnangs,ctdnaexploratory,pdxcdx) {
		$scope.ihc=ihc;
		$scope.tumourngs=tumourngs;
		$scope.fmblood=fmblood;
		$scope.fmtumour=fmtumour;
		$scope.ctdnangs=ctdnangs;
		$scope.ctdnaexploratory=ctdnaexploratory;
		$scope.pdxcdx=pdxcdx;
	}
	
});
