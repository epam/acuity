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

acuityDockerRepo=https://github.com/digital-ECMT/acuity-docker.git
acuityVahubRepo=https://github.com/digital-ECMT/acuity-vahub
vasecurityRepo=https://github.com/digital-ECMT/acuity-va-security
adminUiRepo=https://github.com/digital-ECMT/acuity-admin.git
configServerRepo=https://github.com/digital-ECMT/acuity-config-server.git
flywayRepo=https://github.com/digital-ECMT/acuity-flyway.git

currentDir=$(pwd)
mvn_image=maven:3.3.9-jdk-8-onbuild

echo "Downloading the source code..."
git clone $acuityDockerRepo
git clone $acuityVahubRepo
git clone $vasecurityRepo
git clone $adminUiRepo
git clone $configServerRepo
git clone $flywayRepo

echo "Creating builds directory in acuity-docker folder"
mkdir acuity-docker/building-mode/builds
echo "Creating temporary maven artifact folder..."

#For some reason maven stucks while downloading dependencies related to this plugin
#As a workaround we explicitly install necessary plugin to local repository before start building main source code.
#If this dependency is changed in Vahub in future, this dependency should correspondingly be updated.
wget https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-war-plugin/2.2/maven-war-plugin-2.2.pom
docker run -it --rm -v "$(pwd)":/usr/src/mymaven -v "$currentDir/maven-repo":/root/.m2 -w /usr/src/mymaven $mvn_image /bin/bash -c \
"export MAVEN_OPTS=\"$MAVEN_OPTS -Djava.net.preferIPv6Stack=true -Dgenerate.pom=true\"; \
mvn install -f maven-war-plugin-2.2.pom; \
cd va-security;mvn clean install -DskipTests;cd ../vahub; \
mvn clean package -P webapp -DskipTests;cd ../acuity-admin; \
mvn clean install -DskipTests;cd ../acuity-flyway; \
mvn clean install -DskipTests;cd ../acuity-config-server; \
mvn clean install -DskipTests"

cd $currentDir

sudo cp vahub/vahub/target/*SNAPSHOT*.war $currentDir/acuity-docker/building-mode/builds/vahub.war
sudo cp acuity-admin/acuity-core/target/adminui*.war $currentDir/acuity-docker/building-mode/builds/adminui.war
sudo cp va-security/web/target/va-security*.war $currentDir/acuity-docker/building-mode/builds/vasecurity.war
sudo cp acuity-flyway/target/acuity-flyway*.jar $currentDir/acuity-docker/building-mode/builds/acuity-flyway.jar
sudo cp acuity-config-server/target/acuity-config-server*.war $currentDir/acuity-docker/building-mode/builds/acuity-config-server.war

sudo rm -rf maven-repo acuity-admin acuity-config-server acuity-flyway va-security vahub target
sudo rm maven-war-plugin-2.2.pom
docker rmi $mvn_image
