## Description
After building `acuity-docker` backbone on your VM you will need to configure Azure SSO authentication and file shares,\
and provide SSL protection for your app.

This directory contains scripts for configuring remote Azure VM where docker and `acuity-docker` repository was installed.\
These scripts serve the following:
* `azure-configuration` scripts for setting up azure credentials contained in files yielded from `setup-azure-environment-via-cli.sh` execution.\
  Step is required for sso-authentication mechanism setup
* `ssl` directory contains two scripts to automate SSL-certificate installation
* `set-up-database.sh` - inits database and sets fist global admin for ACUITY first boot.



## Configure Azure file share and SSO authentication
`azure-configuration` directory contains 2 scripts: `set-up-az-storage-and-sso.sh` and `apply-azure-configurations.py`
* Upload them to your virtual machine to `/home/(your_vm_username)/` folder.\
* Make sure this directory contains 
`acuity-docker` and `azure-credentials` directory provided by `setup-azure-environment-via-cli.sh` script execution. If the last folder not exists on your VM, check the directory on you local machine where this script was executed and upload this folder to your VM.
* Launch `set-up-az-storage-and-sso.sh` from your VM

Credentials from `azure-credentials` content would be copied to configs of `acuity-docker`
## SSL-installation
You have two options to install SSL-certificates in application:
#### 1. Use trusted certificate authority:
Purchase certificate for your domain name and upload them to `/home/(your_vm_username)/` directory./
Upload `install-SSL.sh` script to your virtual machine and run it.
#### 2. Generate self-signed certificates.  
This method is temporary from the cyber-security perspective. The idea is to create a root certificate and SSL-certificate based on it.\
You could then import created `root.crt` (Root certificate) to Trusted Root Certificate Store in order to make machine accessing ACUITY recognize SSL-connection as valid.
You will need to:
* Upload `install-self-signed-certificates.sh` script to `/home/(your_vm_username)/` of your VM.
* Run the script. During execution, you would be prompted several times to set root-certificates parameters and enter your domain name and public ip address. \
  Root certificate parameters would be set to default names in case of skipping by and are not strictly necessary to be modified *but*
  **public ip and domain name of your VM are mandatory to set**
* Download generated `root.crt` to machines which would be set to trust this certificate and follow next steps.
* Open your Web-browser
* Open the `Settings` page
  find `Privacy and Security` section, open `Security`, then find and open `Manage certificates`\
* In the new window open the tab `Trusted Root Certificate Authorities`, then press Import...;
* In the import wizard, choose your root.crt certificate, after that just follow the wizard instructions without changing any options. When your press Finish and then Yes, the certificate will be added.

## Set up database
* Upload and run `set-up-database.sh` script from the `/home/(your_vm_username)/` directory of your VM.
* You will be prompted to set the email of the first global admin. Set your email. In the future you could add extra global admins from VA-Security application interface.

### Note:
Sometimes it is necessary to convert script files to Unix-encoding format. From time to time making changes on Windows environment or downloading scripts from git-repositories could break this encoding. To fix this you could use `dos2unix` command line utility or modify your
scripts in `Notepad++` with explicitly declared `Unix` encoding type. In Notepad++ this parameter is configured in the bottom-right corner of the window.
