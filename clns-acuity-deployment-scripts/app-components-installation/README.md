### Directory contains the following scripts:
* `install-docker.sh` - installs **docker** and **docker-compose** to linux OS. Could be used to install these software either on Azure VM or local machine.
* `build-from-sources.sh` - script for installation of `acuity-docker` repository which would contain `.jar` and `.war` assembled from source code of git-repositories.
Prefer this method of installation if you prefer to start ACUITY in building mode. For the sake of speed and convenience please choose installation from ready-to-use docker images and ignore this script.
* `build-from-images.sh` - downloads `acuity-docker` repository to start application from ready docker images. This is a preferable method of installation

### How to use it?
Launch these scripts from the `/home/*your_username_on_vm*/` directory of your Azure VM. 
(Or from the same directory as your `acuity-docker` project)

### Note:
Sometimes it is necessary to convert script files to Unix-encoding format. From time to time making changes on Windows environment or downloading scripts from git-repositories could break this encoding. To fix this you could use `dos2unix` command line utility or modify your
scripts in `Notepad++` with explicitly declared `Unix` encoding type. In Notepad++ this parameter is configured in the bottom-right corner of the window.
