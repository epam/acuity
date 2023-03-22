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

stty -echo
printf "Please enter your VM password (sudo):"
read VM_PASSWORD
stty echo
printf "\n"
echo 'Installing docker using convenience script on Ubuntu OS.'
printf "\n"
echo "For more info please visit https://docs.docker.com/engine/install"
curl -fsSL https://get.docker.com -o get-docker.sh
echo $VM_PASSWORD | sudo -S sh get-docker.sh
#Add docker group to sudo to run docker commands from non-sudo
sudo usermod -aG docker $USER
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
rm get-docker.sh
newgrp docker
