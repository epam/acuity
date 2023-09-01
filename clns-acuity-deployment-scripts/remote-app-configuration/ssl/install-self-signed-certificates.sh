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

echo "Generating Root Certificate ..."

echo "Please note that the next steps are optional and could be skipped!"
printf "\n"

printf "Please enter the name of your country ... "
read country_name
printf "\n"

printf "Please enter the name of state or province ... "
read province_name
printf "\n"

printf "Please enter the name of locality ... "
read locality_name
printf "\n"


printf "Please enter the name of organisation ... "
read org_name
printf "\n"

printf "Please enter the name of organisational Unit ... "
read org_unit_name
printf "\n"

printf "Please enter the root common name ... "
read root_name
printf "\n"

printf "Please enter the organisation email address ... "
read org_email
printf "\n"

echo "
[req]
default_bits       = 2048
distinguished_name = req_distinguished_name
default_md         = sha256
prompt			   = no

[req_distinguished_name]
countryName                = ${countryName:-'CN'}
stateOrProvinceName        = ${province_name:-'Some State'}
localityName               = ${locality_name:-'Some Locality'}
organizationName           = ${org_name:-'Some Organization'}
organizationalUnitName     = ${org_unit_name:-'Some Unit'}
commonName                 = ${root_name:-'Root Common Name'}
emailAddress			   = ${org_email:-'root@email.em'}
" >> root.cnf


openssl genrsa -out root.key 2048
openssl req -x509 -new -key root.key -days 10000 -out root.crt -config root.cnf
echo "Root certificate generated..."

printf "Please enter the your domain name! Ex. - some.app.domain.name.com "
printf "\n"
printf "Please note that this step is mandatory!"
printf "\n"
read dns_address
printf "\n"

printf "Please enter public ip of your virtual vm..."
printf "\n"
printf "Please note that this step is mandatory!"
printf "\n"
read vm_ip
printf "\n"


echo "
[req]
default_bits       = 2048
distinguished_name = req_distinguished_name
x509_extensions    = v3_req
default_md         = sha256
prompt			   = no

[req_distinguished_name]
countryName                = ${countryName:-'CN'}
stateOrProvinceName        = ${province_name:-'Some State'}
localityName               = ${locality_name:-'Some Locality'}
organizationName           = ${org_name:-'Some Organization'}
organizationalUnitName     = ${org_unit_name:-'Some Unit'}
commonName                 = ${vm_ip}
emailAddress			   = ${org_email:-'some@email.em'}

[v3_req]
subjectAltName = @alt_names

[alt_names]
IP.1 = ${vm_ip}
DNS.1 = ${dns_address}
" >> ca.cnf

openssl genrsa -out ca.key 2048
openssl req -out ca.csr -newkey rsa:2048 -nodes -keyout ca.key -config ca.cnf -extensions v3_req
openssl x509 -req -in ca.csr -CA root.crt -CAkey root.key -CAcreateserial -out ca.crt -days 9999 -extfile ca.cnf -extensions v3_req
openssl x509 -in ca.crt -noout -text

acuityDocker=$HOME/acuity/clns-acuity-docker
sudo rm -rf $acuityDocker/ssl-certificates
mkdir -p $acuityDocker/ssl-certificates/certs
mkdir -p $acuityDocker/ssl-certificates/keys/
sudo cp ca.crt $acuityDocker/ssl-certificates/certs/
sudo cp ca.key $acuityDocker/ssl-certificates/keys/

echo "Moving generated ssl certificates to separate directory in current folder ..."
mkdir generated-ssl-certificates
sudo mv *.csr *.crt *.cnf *.key *.srl ./generated-ssl-certificates

echo "Now you should install root.crt file to your local machine to make it trust root certificate you've just created. For the sake of cyber-security change your self-signed certificates to authorized ones as soon as possible."
