[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Project overview
`acuity-config-server`
project contains a simple application that provide Spring configuration data to other ACUITY applications based on the provided config files set (see below for details). 
To install ACUITY please refer to [main project repository](https://github.com/digital-ECMT/acuity-docker)
 and [project's Wiki](https://github.com/digital-ECMT/acuity-docker/wiki/Applications-Setup)
<hr>

## Developer's section

### Building
To build the application, run when in `<root_dir> (acuity-config-server)` (you need Maven installed):
```shell script
mvn clean package
```
Then you'll receive the built `acuity-config-server-<version>.war` file in `acuity-config-server/target` directory.

### Configuration
To prepare the application start place the Spring config file set you want to provide to the directory `/usr/root/acuity-spring-configs` (or you may switch it to another different directory in the `application.yml` file or override the `spring.cloud.config.server.native.search-locations` variable when running).

### Running
To start the application, just run the following (you need Java installed):
```shell script
java -jar /path/to/acuity-config-server.war
```
### Use
When running, the application provides configuration data through the endpoint `http://<your-host>:8888//acuity-spring-configs`, so this endpoint should be set in other ACUITY applications start configuration to make them receive their configs. 
