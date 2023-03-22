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

current_dir=$(pwd)
rm ./acuity-docker/ssl-certificates/certs/*
rm ./acuity-docker/ssl-certificates/keys/*
mkdir -p $current_dir/acuity-docker/ssl-certificates/certs
mkdir -p $current_dir/acuity-docker/ssl-certificates/keys
sudo cp ./ca.crt $current_dir/acuity-docker/ssl-certificates/certs/ || (echo "Supply your ca.crt and ca.key to the root of your vm-space where acuity-docker directory is persisted..."  && exit)
sudo cp ./ca.key $current_dir/acuity-docker/ssl-certificates/keys/ || (echo "Supply your ca.crt and ca.key to the root of your vm-space where acuity-docker directory is persisted..."  && exit)
cd acuity-docker || (echo "You should run the script from root folder with acuity-docker in scope." && exit)
cd acuity-docker || (echo "You should run the script from root folder with acuity-docker in scope." && exit)
docker-compose -f docker-compose_building-mode.yml restart httpd | (echo "No need to reboot httpd...")
