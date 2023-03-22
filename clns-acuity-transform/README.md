[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### What is ACUITY-transform (SDTM-tool) ?

`ACUITY-transform` is a tool which helps you in pre-processing complex datasets to further load them into 
[VA-Hub](https://github.com/digital-ECMT/vahub) 

For instance, you have multiple source files with data which you would like to somehow combine into one file
(convert dates, extract significant keywords, etc) and map it into [AdminUI](https://github.com/digital-ECMT/acuity-admin) afterwards.

<b>Following the [wiki-scheme](https://github.com/digital-ECMT/acuity-docker/wiki#project-architecture-overview) ACUITY-transform is a SDTM-Converter for preparing ACUITY Raw data for subsequent load in ACUITY VaHub. It can convert complex .sas7bdat and .csv files into simpler .csv files for subsequent load into ACUITY VaHub.
This is an `auxiliary` utility, not obligatory for running ACUITY</b>

`ACUITY-transform` is usually used on-demand to prepare data files locally, but also it could be deployed on remote server to run on schedule

<hr>

## Developer's FAQ

### Create executable file

App is build by Gradle.

Build artifact locally with Gradle (Tasks -> build -> assemble).
or by terminal command `gradlew assemble` from root of project. `jar` files will appear in `build/libs` folder of project


### How to run ACUITY-transform locally

1) Install the Mongo database on your machine if this's your first time with `acuity-transform` project.  
2) Configure VM options for `bootRun` gradle task like this `-Dspring.profiles.active=local -Dspring.config.location=../resources`.  
3) Define `{SRC_BASE}` and `{OUT_BASE}` directories for files in the `application-local.yml`, required for input and output files accordingly.  
4) Put the Dummy files in `{SRC_BASE}` directory in appropriate folder for studies from the list in `application-local.yml`.  
5) Start `mongod` service from the bin directory of installed mongodb or via cmd.  
6) Execute gradle bootRun and check output files in `{OUT_BASE}` directory.  

Example of the `application-local.yml`:  
`mongo:`  
&nbsp;&nbsp;`host: localhost`  
&nbsp;&nbsp;`port: 27017`  
&nbsp;&nbsp;`db: test`  

`SRC_BASE: C:/Temp`  
`OUT_BASE: C:/Temp`  

`studies:`  
&nbsp;&nbsp;`list:` -- list of studies  
&nbsp;&nbsp;&nbsp;&nbsp;`- study: STUDY001` -- study's name  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`version: SDTM_1_1_STUDY001` -- processor's name for study  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`source: ${SRC_BASE}/STUDY001` -- folder with input files  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`destination: ${OUT_BASE}/sdtm/STUDY001` -- folder with output files  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`domain: dm`  
&nbsp;&nbsp;&nbsp;&nbsp;`- study: STUDY002`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`version: SDTM_1_1_STUDY002`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`source: ${SRC_BASE}/STUDY002`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`destination: ${OUT_BASE}/sdtm/STUDY002`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`domain: dm`  
&nbsp;&nbsp;&nbsp;&nbsp;`- study: STUDY003`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`version: SDTM_1_1_STUDY003`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`source: ${SRC_BASE}/STUDY003`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`destination: ${OUT_BASE}/sdtm/STUDY003`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`domain: dm`

**Pay attention** that there is no need to create the folder for output files in the file system of Windows 
(you can only specify the name of the output folder in .yml file). 
But you have to create the folder when you're working with Azure Storage to avoid unwanted exceptions during the transformation process.

### How to deploy ACUITY-transform

#### Preparations
1) Build artifact locally with Gradle (Tasks -> build -> assemble).  
2) If there is no `acuity-transform` folder in the user's working directory on the target environment,
create the folder and set up mongodb on the RHEL server by [guide](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-red-hat/).  
**Do not forget** to start it by `service mongod start`.  
3) If there is `acuity-transform-[version]-SNAPSHOT.jar` in the `acuity-transform` folder rename this file to `acuity-transform-[version]-SNAPSHOT-legacy.jar`.   
4) Copy `acuity-transform-[version]-SNAPSHOT.jar` from `/build/libs/` to `acuity-transform` folder on the target server.  
5) Edit (create/copy) appropriate configuration files and scripts for the current environment from the `src/main/resources` folder to `acuity-transform` folder on the target server.
There should be files listed below:  
    * `application-dev.yml`;  
    * `application-prod.yml`;  
    * `acuity-transform-dev.sh`;  
    * `acuity-transform-prod.sh`.  
      
Files with the `-dev` suffix are for your manual run. Files with the `-prod` suffix are for the server's cron job.  
It is not necessary to update `acuity-transform-*.sh` files each time, but you have to update `application-*.yml` configuration files.  
  

#### Run & check  
6) Run `./acuity-transform-dev.sh` from `acuity-transform` folder on the target server (don't do it with `sudo`).  
7) Go to the Azure explorer for the target environment to check if there are some new files after SDTM transformation.  
**Do not download** the files because they could contain sensitive information which we are not allowed to get. 
So just check files existence.
8) Configure the cron job which would be run the script every night:  
`crontab -e` (this command open **crontab** file in VIM editor) in console and put in the file this line `0 22 * * * cd ~/acuity-transform && ./acuity-transform-prod.sh`, 
save it then open one more time to make sure that cron job was saved. 
**Do not forget** to check the status of the cron service `service crond status` and start it if it is stopped.
