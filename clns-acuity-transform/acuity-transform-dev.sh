#!/bin/bash

#
# Copyright 2021 The University of Manchester
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

PATH=~/mongodb/bin:$PATH
mongod --port=27017 --dbpath=logs/db --shutdown

rm -rf logs
mkdir -p logs

mkdir -p logs/db
mongod --port=27017 --dbpath=logs/db --fork --nounixsocket --logpath=logs/mongodb.log

java -Dspring.profiles.active=azure-storage,dev -jar acuity-transform-9.0-SNAPSHOT.jar

mongod --port=27017 --dbpath=logs/db --shutdown
