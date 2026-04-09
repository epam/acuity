#!/bin/bash -e
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

cd ../..
currentDir=$(pwd)
mvn_image=maven:3.3.9-jdk-8-onbuild

echo "Creating builds directory in acuity-docker folder"
mkdir -p clns-acuity-docker/building-mode/builds
echo "Creating temporary maven artifact folder..."

sudo apt install maven openjdk-8-jdk
sudo update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

#For some reason maven stucks while downloading dependencies related to this plugin
#As a workaround we explicitly install necessary plugin to local repository before start building main source code.
#If this dependency is changed in Vahub in future, this dependency should correspondingly be updated.
wget https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-war-plugin/2.2/maven-war-plugin-2.2.pom
MAVEN_OPTS="$MAVEN_OPTS -Djava.net.preferIPv6Stack=true -Dgenerate.pom=true"
#echo '{"registry": "https://registry.bower.io", "strict-ssl": false}' >~/.bowerrc

#echo 'Installing maven WAR plugin'; mvn install -f maven-war-plugin-2.2.pom
echo 'Building VASecurity'; cd clns-acuity-va-security;mvn install -DskipTests
echo 'Building VAHub'; cd ../clns-acuity-vahub; mvn package -P webapp -DskipTests
echo 'Building Admin'; cd ../clns-acuity-admin; mvn install -DskipTests
echo 'Building Flyway'; cd ../clns-acuity-flyway; mvn install -DskipTests -P docker-flyway -P docker-postgres
echo 'Building Config Server'; cd ../clns-acuity-config-server; mvn install -DskipTests

cd $currentDir

sudo cp clns-acuity-vahub/vahub/target/*SNAPSHOT*.war $currentDir/clns-acuity-docker/building-mode/builds/vahub.war
sudo cp clns-acuity-admin/acuity-core/target/adminui*.war $currentDir/clns-acuity-docker/building-mode/builds/adminui.war
sudo cp clns-acuity-va-security/web/target/va-security*.war $currentDir/clns-acuity-docker/building-mode/builds/vasecurity.war
sudo cp clns-acuity-flyway/target/acuity-flyway*.jar $currentDir/clns-acuity-docker/building-mode/builds/acuity-flyway.jar
sudo cp clns-acuity-config-server/target/acuity-config-server*.war $currentDir/clns-acuity-docker/building-mode/builds/acuity-config-server.war

#sudo rm -rf maven-repo acuity-admin acuity-config-server acuity-flyway va-security vahub target
#sudo rm maven-war-plugin-2.2.pom
#docker rmi $mvn_image
