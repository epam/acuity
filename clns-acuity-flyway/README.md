[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### ACUITY Flyway is designed to apply database migrations to ACUITY database

To install ACUITY please refer to [main project repository](https://github.com/digital-ECMT/acuity-docker)
and [project's Wiki](https://github.com/digital-ECMT/acuity-docker/wiki/Applications-Setup)

<hr>

## Developer's section
##### Common information
This repository contains **Flyway** scripts for **ACUITY** project database initialization and migration. It is intended to use it on a **PostgreSQL 11** server with **orafce** extensions and some other (to learn more about the server installation and setup, please see details on the main project wiki).

##### How to prepare the database for initialization by these scripts
- create user `dbadmin` with superuser permissions
- create database `acuity_db`

##### How to create migration script
- create migration script file in `src\main\resources\flyway\release-<current-release>\migrations` directory
- name this file using pattern `V[unix timestamp in seconds]__[story_and_comments].sql`.
Note that there must be double underscore after the timestamp, otherwise it won't work.

##### How to apply migration
- build the project with maven (`mvn clean install`)
- download and unpack **Flyway console utility 7.0.0** (for Windows, you may download it [here](https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/7.0.0/flyway-commandline-7.0.0.zip)) or later
- place the .jar artifact you've built into `<Flyway directory>/jars`
- copy the `flyway.conf` file into `<Flyway directory>/conf` (replacing the existing file) and update the copy, setting the following values:
    - `flyway.url` should contain real target machine IP instead of sample one
    - `flyway.password` should be real password for `dbadmin` user, on behalf of which all Flyway scripts will be performed
    - `flyway.placeholders.user.acuity.password` should be real password for `acuity` user, on behalf of which ACUITY applications will interact with the DB
- run in command line:
    - `flyway info` to check status
    - `flyway migrate` to apply migrations
