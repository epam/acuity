#!/usr/local/bin/python
import yaml
import os

current_dir = os.getcwd()

print("Setting up credentials for VaSecurity...")
with open('{0}/azure-credentials/vasecurity-sso.yml'.format(current_dir),'r') as file:
    secrets = yaml.safe_load(file)
with open('{0}/acuity-docker/acuity-spring-configs/vasecurity-azure-sso.yml'.format(current_dir),'r') as dest_file:
    destination_file = yaml.safe_load(dest_file)
with open('{0}/acuity-docker/acuity-spring-configs/vasecurity-azure-sso.yml'.format(current_dir),'w') as dest_file:
    destination_file['azure']['resource']['clientId']=secrets['clientId']
    destination_file['azure']['resource']['clientSecret']=secrets['clientSecret']
    destination_file['azure']['resource']['preEstablishedRedirectUri']=secrets['redirectUrl']
    destination_file['azure']['client']['clientId']=secrets['clientId']
    destination_file['azure']['client']['clientSecret']=secrets['clientSecret']
    destination_file['azure']['client']['registeredRedirectUri']=secrets['redirectUrl']     
    destination_file = yaml.dump(destination_file,dest_file,default_flow_style=False)
        
print("Setting up credentials for Admin Ui...")
with open('{0}/azure-credentials/admin-sso.yml'.format(current_dir),'r') as file:
    secrets = yaml.safe_load(file)
with open('{0}/acuity-docker/acuity-spring-configs/admin-azure-sso.yml'.format(current_dir),'r') as dest_file:
    destination_file = yaml.safe_load(dest_file)
with open('{0}/acuity-docker/acuity-spring-configs/admin-azure-sso.yml'.format(current_dir),'w') as dest_file:
    destination_file['azure']['resource']['clientId']=secrets['clientId']
    destination_file['azure']['resource']['clientSecret']=secrets['clientSecret']
    destination_file['azure']['client']['clientId']=secrets['clientId']
    destination_file['azure']['client']['clientSecret']=secrets['clientSecret']
    destination_file = yaml.dump(destination_file,dest_file,default_flow_style=False)

print("setting up credentials for Vahub...")
with open('{0}/azure-credentials/vahub-sso.yml'.format(current_dir),'r') as file:
    secrets = yaml.safe_load(file)
with open('{0}/acuity-docker/acuity-spring-configs/vahub-azure-sso.yml'.format(current_dir),'r') as dest_file:
    destination_file = yaml.safe_load(dest_file)
with open('{0}/acuity-docker/acuity-spring-configs/vahub-azure-sso.yml'.format(current_dir),'w') as dest_file:
    destination_file['azure']['resource']['clientId']=secrets['clientId']
    destination_file['azure']['resource']['clientSecret']=secrets['clientSecret']
    destination_file['azure']['client']['clientId']=secrets['clientId']
    destination_file['azure']['client']['clientSecret']=secrets['clientSecret']
    destination_file = yaml.dump(destination_file,dest_file,default_flow_style=False)

print ("setting up credentials for Azure Storage...")
with open('{0}/azure-credentials/admin-azure-storage.yml'.format(current_dir),'r') as file:
    secrets = yaml.safe_load(file)
with open('{0}/acuity-docker/acuity-spring-configs/admin-azure-storage.yml'.format(current_dir),'r') as dest_file:
    destination_file = yaml.safe_load(dest_file)
with open('{0}/acuity-docker/acuity-spring-configs/admin-azure-storage.yml'.format(current_dir),'w') as dest_file:
    destination_file['azure']['storage']['account']=secrets['account']
    destination_file['azure']['storage']['key']=secrets['key']
    destination_file = yaml.dump(destination_file,dest_file,default_flow_style=False)    

print("Setting up common config for application...")
with open('{0}/azure-credentials/application-azure-sso.yml'.format(current_dir),'r') as file:
    secrets = yaml.safe_load(file)
with open('{0}/acuity-docker/acuity-spring-configs/application-azure-sso.yml'.format(current_dir),'r') as dest_file:
    destination_file = yaml.safe_load(dest_file)
with open('{0}/acuity-docker/acuity-spring-configs/application-azure-sso.yml'.format(current_dir),'w') as dest_file:
    destination_file['azure']['resource']['accessTokenUri']=secrets['accessTokenUri']
    destination_file['azure']['resource']['userAuthorizationUri']=secrets['userAuthorizationUri']
    destination_file['azure']['client']['accessTokenUri']=secrets['accessTokenUri']
    destination_file['azure']['client']['userAuthorizationUri']=secrets['userAuthorizationUri']
    destination_file['azure']['logoutUrl']=secrets['logoutUrl']
    destination_file['azure']['authorityUri']=secrets['authorityUri']
    destination_file = yaml.dump(destination_file,dest_file,default_flow_style=False)
    

