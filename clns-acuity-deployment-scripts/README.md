[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
## Concept:

Purposes of this script repository are the following:

* automate Azure Environment set up (VM, file-share, backups, resource groups, app registrations for Active Directory
  Authentication)
* automate SSL-certificate installation
* automate ACUITY app installation
* automate sso-authentication mechanism set up
* help while setting up fist global admin on ACUITY first boot

## How to use this deployment script repository?

Repository contains several folders with scripts for several purposes:\
1.`azure-env-setup`- scripts for setting up Azure Environment with Virtual Machine assigned to public IP address and
domain name in Azure default subdomain. \
2.`app-components-installation`- directory containing scripts for installation of docker and ACUITY application.\
3.`remote-app-configuration` - scripts for configuring azure-sso (**Active Directory Authentication** and **Azure File
Share**)

#### To set up ACUITY in **Azure-cloud**

Please use these directories in **sequential** order mentioned above. \
For more detailed instructions regarding usage of specific scripts please refer to **readme** files in corresponding
directories

#### To start up ACUITY locally on your Windows machine

Make sure you are following prerequisites described below, then download [acuity-docker](https://github.com/digital-ECMT/acuity-docker) repository via **zip**-archive
or `git clone` and follow **README** instructions described in `startup` directory

## Prerequisites

1.We assume you have Linux Ubuntu distribution installed to run `.sh` scripts in this repository. Please
use [WSL2](https://docs.microsoft.com/en-us/windows/wsl/install-win10) for Windows 10 to be able to run them. \
Ubuntu Linux distribution can be installed from the official Microsoft Store.  
2.You will need [Docker](https://www.docker.com/) to run application locally.\
To set up Docker on Windows desktop you could
install [Docker desktop](https://docs.docker.com/docker-for-windows/install/) **or** run `install-docker.sh` script
from `app-components-installation` folder.\
To install Docker you should have WSL2 installed.

### Note:

Sometimes it is necessary to convert script files to Unix-encoding format. From time to time making changes on Windows
environment or downloading scripts from git-repositories could break this encoding. To fix this you could use `dos2unix`
command line utility or modify your scripts in `Notepad++` with explicitly declared `Unix` encoding type. In Notepad++
this parameter is configured in the bottom-right corner of the window.

<b> 
Launched scripts of this repository should be in the same path as your `acuity-docker` directory. Please consider it after `acuity-docker` installation.
</b>
