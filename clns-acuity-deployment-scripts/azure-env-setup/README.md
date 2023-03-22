### Description.
Use `setup-azure-environment-via-cli.sh` script to set up Azure Environment where ACUITY application will be deployed.\
This script will install:
* resource and web-security groups
* virtual machine under public ip address and domain name in `.cloudapp.azure.com` subdomain
* backup vault for storing virtual machine backups (in the end of script execution first backup will be provided)
* app registrations for enabling usage of Azure **SSO authentication** mechanism and **Azure File Share**.\
  Credentials and secrets for these entities would be stored in the newly-created folder `azure-credentials` in the directory of this script execution and transferred to your
  created VM afterwards.
 
### Prerequisites:
* Linux shell to execute this script (use `bash` of WSL2).
* Your Linux system should have `az cli` installed (Azure Command Line Interface). \
  To install it please refer to [official documentation](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli-linux?pivots=script)
* You should be logged into you Azure Subscription under which you are going to create your environment.


### How to use this script?
1. Open `setup-azure-environment-via-cli.sh` script with any text editor and fill all parameters in the top of the file.\
Follow the examples and instructions placed in the comments above each parameter. Save the file.
2. Run the file.
3. During script execution you will be prompted several times to set up your VM's password and to permit SSH-connection to your newly created virtual machine.

### Note:
Sometimes it is necessary to convert script files to Unix-encoding format.
From time to time making changes on Windows environment or downloading scripts from git-repositories could break this encoding. 
To fix this you could use `dos2unix` command line utility or modify your scripts in `Notepad++` with explicitly declared `Unix` encoding type.
In Notepad++ this parameter is configured in the bottom-right corner of the window.
