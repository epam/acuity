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

#Name of your Azure Subscription where resources will be deployed. You could query it via CLI - [ az account show --query "name" -o tsv]
SUBSCRIPTION=
#Short name of Subscription no more than 5 characters. Do not use anything except letters and numbers
SUBSCRIPTION_SHORT=
# This is the region where resources will be deployed to, change to a region of your choice more information on available regions run the following command 'az account list-locations -o table'
LOCATION=ukwest
# Primary contact is used as a Tag value
PRIMARY_CONTACT=
# Name of environment Dev, Test, Prod
ENVIRONMENT=
#Name of Application no more than 5 characters
APPLICATION=
# This is the username used to log on initially to the VMs post deployment
USERNAME=
#Backup
BACKUP_RESOURCE_GROUP=""
BACKUP_POLICY=""
# Name must start with a letter, and can contain only letters, numbers or hyphens! ex. - "backup-vault"
BACKUP_VAULT=""
# Name of the application Resource Group. Use only lowercase letters and digits! ex. - "rctgrp"
RESOURCE_GROUP_NAME=""
# Storage
STORAGE_RESOURCE_GROUP=""
#Name of Azure storage account for files - only lowercase letters and digits! - ex. - "acc01uk"
STORAGE_ACCOUNT_NAME=
###################### NETWORK STUFF ########################################
# Network
NETWORK_RESOURCE_GROUP=""
#This will create a default public dns name of type {name}.{location}.cloudapp.azure.com
DNS_NAME=
########################## VM STUFF (DO NOT CHANGE) #############################################
WEB_VM_SKU="Standard_D2s_v3"
WEB_VM_IMAGE="Canonical:UbuntuServer:18.04-LTS:latest"
ENVIRONMENT_UPPER=${ENVIRONMENT^^}
APPLICATION_NAME=${SUBSCRIPTION_SHORT}${APPLICATION}_W01
################### END OF VARIABLE ASSIGNMENT ##############################################
#Passwords should be at least length of 12 characters. Include numbers , upper-case and lower-case letters.
# Prompt user to set Password for VM admin account
stty -echo
printf "Please set VM password for user account ${USERNAME}: "
read VM_PASSWORD
stty echo
printf "\n"

# Login to azure using your credentials
az account show 1>/dev/null

if [ $? != 0 ]; then
  az login
fi

### Create Resource Group.

echo "Creating Resource Groups please wait......"

if [[ $(az group exists --subscription $SUBSCRIPTION --name $NETWORK_RESOURCE_GROUP) == false ]]; then
  az group create -l $LOCATION -n $NETWORK_RESOURCE_GROUP --subscription $SUBSCRIPTION --tags "Subscription=$SUBSCRIPTION" "Primary Contact=$PRIMARY_CONTACT" "Environment=${ENVIRONMENT_UPPER}" "Application=${APPLICATION}"
else
  echo "Resource Group Exists, skipping"
fi

if [[ $(az group exists --subscription $SUBSCRIPTION --name $STORAGE_RESOURCE_GROUP) == false ]]; then
  az group create -l $LOCATION -n $STORAGE_RESOURCE_GROUP --subscription $SUBSCRIPTION --tags "Subscription=$SUBSCRIPTION" "Primary Contact=$PRIMARY_CONTACT" "Environment=${ENVIRONMENT_UPPER}" "Application=${APPLICATION}"
else
  echo "Resource Group Exists, skipping"
fi

if [[ $(az group exists --subscription $SUBSCRIPTION --name $BACKUP_RESOURCE_GROUP) == false ]]; then
  az group create -l $LOCATION -n $BACKUP_RESOURCE_GROUP --subscription $SUBSCRIPTION --tags "Subscription=$SUBSCRIPTION" "Primary Contact=$PRIMARY_CONTACT" "Environment=${ENVIRONMENT_UPPER}" "Application=${APPLICATION}"
else
  echo "Resource Group Exists, skipping"
fi

## Create Storage Accounts. 2 Storage Accounts are created one for storing diagnostic data, the other for storing the data which will be used by ACUITY.

echo "Creating Storage Accounts - Please Wait......."

az storage account create \
  --name ${RESOURCE_GROUP_NAME}nediag \
  --resource-group $STORAGE_RESOURCE_GROUP \
  --https-only true \
  --subscription $SUBSCRIPTION \
  --kind StorageV2 \
  --sku Standard_LRS \
  --location $LOCATION \
  --allow-blob-public-access false \
  --tags "Subscription=$SUBSCRIPTION" "Primary Contact=$PRIMARY_CONTACT" "Environment=${ENVIRONMENT_UPPER}" "Application=${APPLICATION}"

az storage account create \
  --name ${RESOURCE_GROUP_NAME}nelrs01 \
  --resource-group $STORAGE_RESOURCE_GROUP \
  --https-only true \
  --subscription $SUBSCRIPTION \
  --kind StorageV2 \
  --sku Standard_LRS \
  --location $LOCATION \
  --allow-blob-public-access false \
  --tags "Subscription=$SUBSCRIPTION" "Primary Contact=$PRIMARY_CONTACT" "Environment=${ENVIRONMENT_UPPER}" "Application=${APPLICATION}"
  

## Create Backup Vault & Policy. Used for storing snapshots and VM backups. 
 
if [[ $(az backup vault show --name $BACKUP_VAULT --resource-group $BACKUP_RESOURCE_GROUP --subscription $SUBSCRIPTION) == "${BACKUP_VAULT}" ]]; then
  echo "Backup Vault Exists"
else
  az backup vault create --name $BACKUP_VAULT \
    --resource-group $BACKUP_RESOURCE_GROUP \
    --subscription $SUBSCRIPTION \
    --location $LOCATION
fi 

if [[ $(az backup policy show --subscription $SUBSCRIPTION --name $BACKUP_POLICY --resource-group $BACKUP_RESOURCE_GROUP --vault-name $BACKUP_VAULT) == "${BACKUP_POLICY}" ]]; then
  echo "Backup Policy Exists"
else
  #az backup policy create --subscription $SUBSCRIPTION --policy  --resource-group $BACKUP_RESOURCE_GROUP --vault-name $BACKUP_VAULT --name $BACKUP_POLICY --backup-management-type AzureIaasVM
  ## Create Backup Policy N.B. No documented way to do it via Azure CLI. Policy can be set via JSON input.
  az backup policy set --policy '{
  "name": "'${BACKUP_POLICY}'",
  "properties": {
    "backupManagementType": "AzureIaasVM",
    "instantRpRetentionRangeInDays": 2,
    "protectedItemsCount": 0,
    "retentionPolicy": {
      "dailySchedule": {
        "retentionDuration": {
          "count": 180,
          "durationType": "Days"
        },
        "retentionTimes": [
          "2019-07-09T07:30:00+00:00"
        ]
      },
      "monthlySchedule": {
        "retentionDuration": {
          "count": 60,
          "durationType": "Months"
        },
        "retentionScheduleDaily": null,
        "retentionScheduleFormatType": "Weekly",
        "retentionScheduleWeekly": {
          "daysOfTheWeek": [
            "Sunday"
          ],
          "weeksOfTheMonth": [
            "First"
          ]
        },
        "retentionTimes": [
          "2019-07-09T07:30:00+00:00"
        ]
      },
      "retentionPolicyType": "LongTermRetentionPolicy",
      "weeklySchedule": {
        "daysOfTheWeek": [
          "Sunday"
        ],
        "retentionDuration": {
          "count": 12,
          "durationType": "Weeks"
        },
        "retentionTimes": [
          "2019-07-09T07:30:00+00:00"
        ]
      },
      "yearlySchedule": {
        "monthsOfYear": [
          "January"
        ],
        "retentionDuration": {
          "count": 10,
          "durationType": "Years"
        },
        "retentionScheduleDaily": null,
        "retentionScheduleFormatType": "Weekly",
        "retentionScheduleWeekly": {
          "daysOfTheWeek": [
            "Sunday"
          ],
          "weeksOfTheMonth": [
            "First"
          ]
        },
        "retentionTimes": [
          "2019-07-09T07:30:00+00:00"
        ]
      }
    },
    "schedulePolicy": {
      "schedulePolicyType": "SimpleSchedulePolicy",
      "scheduleRunDays": null,
      "scheduleRunFrequency": "Daily",
      "scheduleRunTimes": [
        "2019-07-09T07:30:00+00:00"
      ],
      "scheduleWeeklyFrequency": 0
    },
    "timeZone": "UTC"
  },
  "resourceGroup": "'${BACKUP_RESOURCE_GROUP}'",
  "tags": null,
  "type": "Microsoft.RecoveryServices/vaults/backupPolicies"
}' --resource-group $BACKUP_RESOURCE_GROUP --vault-name $BACKUP_VAULT --subscription $SUBSCRIPTION
fi

### Create VM

echo "Creating Web Server..."
  
### Create Web VM using Subnet ID. N.B. Created without a Public IP, i.e. "--public-ip-address """
az vm create --name $APPLICATION_NAME \
  --resource-group $NETWORK_RESOURCE_GROUP \
  --subscription $SUBSCRIPTION \
  --location $LOCATION \
  --size $WEB_VM_SKU \
  --image $WEB_VM_IMAGE \
  --admin-username $USERNAME \
  --public-ip-address-dns-name $DNS_NAME \
  --public-ip-address $DNS_NAME \
  --public-ip-address-allocation static \
  --admin-password "${VM_PASSWORD}" \
  --boot-diagnostics-storage ${RESOURCE_GROUP_NAME}nediag \
  --storage-sku StandardSSD_LRS \
  --os-disk-size-gb 128 \
  --tags "Subscription=$SUBSCRIPTION" "Primary Contact=$PRIMARY_CONTACT" "Environment=${ENVIRONMENT_UPPER}" "Application=${APPLICATION}" "Function=Web Server" \
  --nsg ""

## Clears VM password
VM_PASSWORD=""

#az network nsg rule create \
#    --resource-group ${NETWORK_RESOURCE_GROUP} \
#    --nsg-name ${APPLICATION_NAME}NSG \
#    --name in-443 \
#    --protocol tcp \
#    --direction inbound \
#    --priority 1010 \
#    --source-address-prefix '*' \
#    --source-port-range '*' \
#    --destination-address-prefix '*' \
#    --destination-port-range 443 \
#    --access allow
	
#az network nsg rule create \
#    --resource-group ${NETWORK_RESOURCE_GROUP} \
#    --nsg-name ${APPLICATION_NAME}NSG \
#    --name in-444 \
#    --protocol tcp \
#    --direction inbound \
#    --priority 1020 \
#    --source-address-prefix '*' \
#    --source-port-range '*' \
#    --destination-address-prefix '*' \
#    --destination-port-range 444 \
#    --access allow

#az network nsg rule create \
#    --resource-group ${NETWORK_RESOURCE_GROUP} \
#    --nsg-name ${APPLICATION_NAME}NSG \
#    --name in-447 \
#    --protocol tcp \
#    --direction inbound \
#    --priority 1030 \
#    --source-address-prefix '*' \
#    --source-port-range '*' \
#    --destination-address-prefix '*' \
#    --destination-port-range 447 \
#    --access allow

vasecurity_url=https://${DNS_NAME}.${LOCATION}.cloudapp.azure.com:444
adminui_url=https://${DNS_NAME}.${LOCATION}.cloudapp.azure.com:447
vahub_url=https://${DNS_NAME}.${LOCATION}.cloudapp.azure.com:443

echo "Generating application registries..."
vasecurity_app_id=$((az ad app create --display-name ${APPLICATION}-vasecurity --web-redirect-uris ${vasecurity_url}/login --query "appId") | xargs)
admin_app_id=$((az ad app create --display-name ${APPLICATION}-admin --web-redirect-uris ${adminui_url}/login --query "appId") | xargs)
vahub_app_id=$((az ad app create --display-name ${APPLICATION}-vahub  --web-redirect-uris ${vahub_url}/login --query "appId") | xargs)

echo "Adding Microsoft Graph User.Read permissions to registries..."
az ad app permission add --id $vahub_app_id --api 00000003-0000-0000-c000-000000000000 --api-permissions e1fe6dd8-ba31-4d61-89e7-88639da4683d=Scope
az ad app permission add --id $vasecurity_app_id --api 00000003-0000-0000-c000-000000000000 --api-permissions e1fe6dd8-ba31-4d61-89e7-88639da4683d=Scope
az ad app permission add --id $admin_app_id --api 00000003-0000-0000-c000-000000000000 --api-permissions e1fe6dd8-ba31-4d61-89e7-88639da4683d=Scope

az ad sp create --id $vahub_app_id
az ad sp create --id $vasecurity_app_id
az ad sp create --id $admin_app_id

echo "Generating application secrets..."
vasecurity_client_secret=$((az ad app credential reset --id ${vasecurity_app_id} --append --years 2 --query "password") | xargs)
admin_client_secret=$((az ad app credential reset --id ${admin_app_id} --append --years 2 --query "password") | xargs)
vahub_client_secret=$((az ad app credential reset --id ${vahub_app_id} --append --years 2 --query "password") | xargs)


echo "Creating Azure storage account..."
az storage account create --name $STORAGE_ACCOUNT_NAME --resource-group $NETWORK_RESOURCE_GROUP --location $LOCATION --kind StorageV2 --allow-blob-public-access false

storage_key=$((az storage account keys list -g $NETWORK_RESOURCE_GROUP -n $STORAGE_ACCOUNT_NAME --query [0].value) | xargs)

echo 'Exporting Azure Credentials to file...'
mkdir azure-credentials

echo "clientId: ${vasecurity_app_id}" >> ./azure-credentials/vasecurity-sso.yml
echo "clientSecret: ${vasecurity_client_secret}" >> ./azure-credentials/vasecurity-sso.yml
echo "redirectUrl: ${vasecurity_url}/login" >> ./azure-credentials/vasecurity-sso.yml

echo "clientId: ${admin_app_id}" >> ./azure-credentials/admin-sso.yml
echo "clientSecret: ${admin_client_secret}" >> ./azure-credentials/admin-sso.yml
echo "redirectUrl: ${adminui_url}/login" >> ./azure-credentials/admin-sso.yml

echo "clientId: ${vahub_app_id}" >> ./azure-credentials/vahub-sso.yml
echo "clientSecret: ${vahub_client_secret}" >> ./azure-credentials/vahub-sso.yml
echo "redirectUrl: ${vahub_url}/login" >> ./azure-credentials/vahub-sso.yml

echo "key: ${storage_key}" >> ./azure-credentials/admin-azure-storage.yml
echo "account: ${STORAGE_ACCOUNT_NAME}" >> ./azure-credentials/admin-azure-storage.yml

tenant_id=$(az account show --query "tenantId" | xargs)
oauth_base_url=https://login.microsoftonline.com/${tenant_id}

echo "accessTokenUri: ${oauth_base_url}/oauth2/token" >> ./azure-credentials/application-azure-sso.yml
echo "userAuthorizationUri: ${oauth_base_url}/oauth2/authorize" >> ./azure-credentials/application-azure-sso.yml
echo "logoutUrl: ${oauth_base_url}/oauth2/logout" >> ./azure-credentials/application-azure-sso.yml
echo "authorityUri: ${oauth_base_url}/" >> ./azure-credentials/application-azure-sso.yml


vm_ip=$(az vm show -g $NETWORK_RESOURCE_GROUP -n $APPLICATION_NAME -d --query "publicIps" | xargs)


echo "Copying prepared Azure Active Directory configuration credentials to remote VM..."
echo "Please accept SSH-connection"
rsync -r azure-credentials ${USERNAME}@${vm_ip}:
echo "Done!"

#Enabling backups for newly created VM
echo "Setting up backup for VM"
az backup protection enable-for-vm \
    --resource-group $BACKUP_RESOURCE_GROUP \
    --vault-name $BACKUP_VAULT \
    --vm $(az vm show -g $NETWORK_RESOURCE_GROUP -n $APPLICATION_NAME --query id | tr -d '"') \
    --policy-name $BACKUP_POLICY

BACKUP_ITEM_NAME=$(az backup job list --resource-group $BACKUP_RESOURCE_GROUP --vault-name $BACKUP_VAULT --query [0].properties.entityFriendlyName | tr -d '"')

#Create backup now
az backup protection backup-now \
    --resource-group $BACKUP_RESOURCE_GROUP \
  	--backup-management-type AzureIaasVM \
    --vault-name $BACKUP_VAULT \
    --container-name $APPLICATION_NAME \
    --item-name $BACKUP_ITEM_NAME
#List backup jobs
az backup job list \
    --resource-group $BACKUP_RESOURCE_GROUP \
    --vault-name $BACKUP_VAULT \
    --output table
