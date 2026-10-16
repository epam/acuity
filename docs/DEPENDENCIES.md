# Library Dependencies

Fully resolved (including transitive) Maven dependency versions for the three
Spring Boot backend applications. Each list merges every module of the
reactor, is deduplicated and sorted by `groupId:artifactId`, and includes all
scopes (test-only artifacts are marked `(test)`). Internal `com.acuity.*`
modules are included for completeness.

Generated: 2026-10-16. Regenerate from the repo root (install va-security
first so admin/vahub resolve current snapshots; use `clean install`, since
stale `target/` output can break the build):

```
mvn -f clns-acuity-va-security/pom.xml -B -P '!checks' -DskipTests clean install

for p in clns-acuity-va-security clns-acuity-vahub clns-acuity-admin; do
  mvn -f $p/pom.xml -B -P '!checks' dependency:list \
      -DincludeScope=test -DoutputFile=target/deps-list.txt
done
```

Then merge each module's `target/deps-list.txt` per application.
`clns-acuity-va-security/web` is not part of the va-security reactor and is
not listed.

## Key Versions

| Library | va-security | vahub | admin |
|---|---|---|---|
| Spring Boot | 4.1.1 | 4.1.1 | 4.1.1 |
| Spring Framework | 7.0.9 | 7.0.9 | 7.0.9 |
| Spring Security | 7.1.1 | 7.1.1 | 7.1.1 |
| Spring Cloud Commons | 5.0.3 | 5.0.3 | 5.0.3 |
| Spring Batch | - | - | 6.0.5 |
| Hibernate Validator | 9.1.3.Final | 9.1.3.Final | 9.1.3.Final |
| Tomcat | 11.0.26 | 11.0.26 | 11.0.26 |
| Jackson 3 | 3.1.5 | 3.1.5 | 3.1.5 |
| Jackson 2 | 2.21.5 | 2.21.7 | 2.21.5 |
| Jersey | 4.0.2 | - | - |
| MyBatis | 3.2.6,3.2.8,3.5.16 | 3.5.16 | 3.5.16 |
| PostgreSQL JDBC | 42.7.13 | 42.7.13 | 42.7.13 |
| Logback | 1.5.38 | 1.5.38 | 1.5.38 |
| Quartz | 2.5.2 | - | 2.5.2 |
| JUnit Jupiter | 6.0.3 | 6.0.3 | 6.0.3 |
| Mockito | 5.23.0 | 5.23.0 | 5.23.0 |

## va-security (`clns-acuity-va-security`)

**Maven modules:** `config`, `common`, `auditlogger`, `domain`, `security`, `rest`, `auth`

256 artifacts.
Artifacts resolved to different versions in different modules: `com.google.guava:guava`, `com.zaxxer:HikariCP`, `commons-collections:commons-collections`, `commons-io:commons-io`, `commons-logging:commons-logging`, `org.apache.commons:commons-lang3`, `org.aspectj:aspectjrt`, `org.aspectj:aspectjweaver`, `org.assertj:assertj-core`, `org.codehaus.janino:janino`, `org.javassist:javassist`, `org.mybatis:mybatis`, `org.objenesis:objenesis`.

```
cglib:cglib-nodep:3.1 (test)
ch.qos.logback:logback-classic:1.5.38
ch.qos.logback:logback-core:1.5.38
com.acuity.visualisations.va-common:va-common-auditlogger:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-auth:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-common:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-config:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-config:tests:9.0-beryllium-SNAPSHOT (test)
com.acuity.visualisations.va-security:va-security-domain:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-security:9.0-beryllium-SNAPSHOT
com.fasterxml.jackson.core:jackson-annotations:2.21
com.fasterxml.jackson.core:jackson-core:2.21.5
com.fasterxml.jackson.core:jackson-databind:2.21.5
com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.21.5
com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.5
com.fasterxml.jackson.jaxrs:jackson-jaxrs-base:2.21.5
com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.21.5
com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations:2.21.5
com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.21.5
com.fasterxml.jackson.module:jackson-module-parameter-names:2.21.5
com.fasterxml:classmate:1.7.3
com.github.ben-manes.caffeine:caffeine:3.2.4
com.github.springtestdbunit:spring-test-dbunit:1.1.0 (test)
com.google.auto.service:auto-service-annotations:1.1.1 (test)
com.google.errorprone:error_prone_annotations:2.49.0
com.google.guava:guava:16.0.1,18.0
com.google.protobuf:protobuf-java:4.35.1 (test)
com.googlecode.flyway:flyway-core:2.2.1 (test)
com.h2database:h2:2.3.232 (test)
com.jayway.awaitility:awaitility:1.6.5 (test)
com.jayway.jsonpath:json-path:2.10.0 (test)
com.opera:operadriver:1.5 (test)
com.opera:operalaunchers:1.1 (test)
com.vaadin.external.google:android-json:0.0.20131108.vaadin1 (test)
com.zaxxer:HikariCP:2.0.1,7.0.2
commons-beanutils:commons-beanutils:1.11.0
commons-codec:commons-codec:1.21.0
commons-collections:commons-collections:3.2.1,3.2.2
commons-digester:commons-digester:2.1
commons-io:commons-io:1.3.2,2.4
commons-jxpath:commons-jxpath:1.3 (test)
commons-logging:commons-logging:1.2,1.3.6
commons-validator:commons-validator:1.11.0
io.micrometer:micrometer-commons:1.17.1
io.micrometer:micrometer-core:1.17.1
io.micrometer:micrometer-jakarta9:1.17.1
io.micrometer:micrometer-observation:1.17.1
io.netty:netty:3.5.2.Final (test)
io.opentelemetry:opentelemetry-api:1.62.0 (test)
io.opentelemetry:opentelemetry-common:1.62.0 (test)
io.opentelemetry:opentelemetry-context:1.62.0 (test)
io.opentelemetry:opentelemetry-exporter-logging:1.62.0 (test)
io.opentelemetry:opentelemetry-sdk:1.62.0 (test)
io.opentelemetry:opentelemetry-sdk-common:1.62.0 (test)
io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:1.62.0 (test)
io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:1.62.0 (test)
io.opentelemetry:opentelemetry-sdk-logs:1.62.0 (test)
io.opentelemetry:opentelemetry-sdk-metrics:1.62.0 (test)
io.opentelemetry:opentelemetry-sdk-trace:1.62.0 (test)
jakarta.activation:jakarta.activation-api:2.1.4
jakarta.annotation:jakarta.annotation-api:3.0.0
jakarta.inject:jakarta.inject-api:2.0.1
jakarta.servlet:jakarta.servlet-api:6.1.0
jakarta.validation:jakarta.validation-api:3.1.1
jakarta.ws.rs:jakarta.ws.rs-api:4.0.0
jakarta.xml.bind:jakarta.xml.bind-api:4.0.5
javax.activation:activation:1.1
javax.mail:mail:1.4.7
javax.servlet:javax.servlet-api:3.1.0
javax.transaction:jta:1.1
javax.xml.bind:jaxb-api:2.2.12
net.bytebuddy:byte-buddy:1.18.11 (test)
net.bytebuddy:byte-buddy-agent:1.18.11 (test)
net.minidev:accessors-smart:2.6.0 (test)
net.minidev:json-smart:2.6.0 (test)
net.sourceforge.cssparser:cssparser:0.9.14 (test)
net.sourceforge.htmlunit:htmlunit:2.15 (test)
net.sourceforge.htmlunit:htmlunit-core-js:2.15 (test)
net.sourceforge.nekohtml:nekohtml:1.9.22 (test)
org.apache.commons:commons-collections4:4.6.0
org.apache.commons:commons-exec:1.1 (test)
org.apache.commons:commons-lang3:3.20.0,3.3.2,3.5
org.apache.commons:commons-math3:3.3
org.apache.httpcomponents.client5:httpclient5:5.6.4
org.apache.httpcomponents.core5:httpcore5:5.4.3
org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
org.apache.httpcomponents:httpclient:4.5.14
org.apache.httpcomponents:httpcore:4.4.16
org.apache.httpcomponents:httpmime:4.3.3 (test)
org.apache.logging.log4j:log4j-api:2.25.5
org.apache.logging.log4j:log4j-to-slf4j:2.25.5
org.apache.tomcat.embed:tomcat-embed-core:11.0.26
org.apache.tomcat.embed:tomcat-embed-el:11.0.26
org.apache.tomcat.embed:tomcat-embed-websocket:11.0.26
org.apiguardian:apiguardian-api:1.1.2 (test)
org.aspectj:aspectjrt:1.8.10,1.9.25.1
org.aspectj:aspectjweaver:1.8.10,1.9.25.1
org.assertj:assertj-core:3.0.0,3.27.7,3.4.1 (test)
org.awaitility:awaitility:4.3.0 (test)
org.bitbucket.b_c:jose4j:0.9.7
org.bouncycastle:bcprov-jdk18on:1.85.2
org.checkerframework:checker-qual:3.55.1 (test)
org.codehaus.janino:commons-compiler:3.1.12
org.codehaus.janino:janino:2.7.8,3.1.12
org.dbunit:dbunit:3.5.2 (test)
org.eclipse.jetty:jetty-http:12.1.12 (test)
org.eclipse.jetty:jetty-io:12.1.12 (test)
org.eclipse.jetty:jetty-util:12.1.12 (test)
org.eclipse.jetty:jetty-websocket:8.1.15.v20140411 (test)
org.glassfish.expressly:expressly:5.0.0
org.glassfish.grizzly:grizzly-framework:4.0.2 (test)
org.glassfish.grizzly:grizzly-http:4.0.2 (test)
org.glassfish.grizzly:grizzly-http-server:4.0.2 (test)
org.glassfish.grizzly:grizzly-http-servlet:4.0.2 (test)
org.glassfish.hk2.external:aopalliance-repackaged:4.0.0-M3
org.glassfish.hk2:class-model:4.0.0-M3
org.glassfish.hk2:hk2:4.0.0-M3
org.glassfish.hk2:hk2-api:4.0.0-M3
org.glassfish.hk2:hk2-core:4.0.0-M3
org.glassfish.hk2:hk2-locator:4.0.0-M3
org.glassfish.hk2:hk2-runlevel:4.0.0-M3
org.glassfish.hk2:hk2-utils:4.0.0-M3
org.glassfish.hk2:osgi-resource-locator:3.0.0
org.glassfish.hk2:spring-bridge:4.0.0-M3
org.glassfish.jersey.containers:jersey-container-grizzly2-http:4.0.2 (test)
org.glassfish.jersey.containers:jersey-container-grizzly2-servlet:4.0.2 (test)
org.glassfish.jersey.containers:jersey-container-servlet:4.0.2
org.glassfish.jersey.core:jersey-client:4.0.2
org.glassfish.jersey.core:jersey-common:4.0.2
org.glassfish.jersey.core:jersey-server:4.0.2
org.glassfish.jersey.ext:jersey-bean-validation:4.0.2
org.glassfish.jersey.ext:jersey-entity-filtering:4.0.2
org.glassfish.jersey.ext:jersey-spring6:4.0.2
org.glassfish.jersey.inject:jersey-hk2:4.0.2
org.glassfish.jersey.media:jersey-media-jaxb:4.0.2 (test)
org.glassfish.jersey.media:jersey-media-json-jackson:4.0.2
org.glassfish.jersey.test-framework.providers:jersey-test-framework-provider-grizzly2:4.0.2 (test)
org.glassfish.jersey.test-framework:jersey-test-framework-core:4.0.2 (test)
org.hamcrest:hamcrest:3.0 (test)
org.hamcrest:hamcrest-core:3.0 (test)
org.hamcrest:hamcrest-library:3.0 (test)
org.hdrhistogram:HdrHistogram:2.2.2
org.hibernate.validator:hibernate-validator:9.1.3.Final
org.ini4j:ini4j:0.5.2 (test)
org.javassist:javassist:3.18.1-GA,3.30.2-GA
org.jboss.logging:jboss-logging:3.6.3.Final
org.jooq:joor:0.9.4
org.jspecify:jspecify:1.0.1
org.junit.jupiter:junit-jupiter:6.0.3 (test)
org.junit.jupiter:junit-jupiter-api:6.0.3 (test)
org.junit.jupiter:junit-jupiter-engine:6.0.3 (test)
org.junit.jupiter:junit-jupiter-params:6.0.3 (test)
org.junit.platform:junit-platform-commons:6.0.3 (test)
org.junit.platform:junit-platform-engine:6.0.3 (test)
org.mockito:mockito-core:5.23.0 (test)
org.mockito:mockito-junit-jupiter:5.23.0 (test)
org.mybatis:mybatis:3.2.6,3.2.8,3.5.16
org.mybatis:mybatis-spring:3.0.5
org.objenesis:objenesis:2.1,3.3 (test)
org.opentest4j:opentest4j:1.3.0 (test)
org.ow2.asm:asm:9.7.1 (test)
org.ow2.asm:asm-commons:9.7
org.postgresql:postgresql:42.7.13 (test)
org.projectlombok:lombok:1.18.46
org.quartz-scheduler:quartz:2.5.2
org.quartz-scheduler:quartz-oracle:2.1.7
org.seleniumhq.selenium:selenium-api:4.43.0 (test)
org.seleniumhq.selenium:selenium-chrome-driver:4.43.0 (test)
org.seleniumhq.selenium:selenium-chromium-driver:4.43.0 (test)
org.seleniumhq.selenium:selenium-firefox-driver:4.43.0 (test)
org.seleniumhq.selenium:selenium-htmlunit-driver:2.44.0 (test)
org.seleniumhq.selenium:selenium-http:4.43.0 (test)
org.seleniumhq.selenium:selenium-ie-driver:4.43.0 (test)
org.seleniumhq.selenium:selenium-java:2.44.0 (test)
org.seleniumhq.selenium:selenium-json:4.43.0 (test)
org.seleniumhq.selenium:selenium-manager:4.43.0 (test)
org.seleniumhq.selenium:selenium-os:4.43.0 (test)
org.seleniumhq.selenium:selenium-remote-driver:4.43.0 (test)
org.seleniumhq.selenium:selenium-safari-driver:4.43.0 (test)
org.seleniumhq.selenium:selenium-support:4.43.0 (test)
org.skyscreamer:jsonassert:1.5.3 (test)
org.slf4j:jcl-over-slf4j:1.7.6 (test)
org.slf4j:jul-to-slf4j:1.7.6
org.slf4j:log4j-over-slf4j:1.7.6
org.slf4j:slf4j-api:1.7.6
org.springframework.boot:spring-boot:4.1.1
org.springframework.boot:spring-boot-actuator:4.1.1
org.springframework.boot:spring-boot-actuator-autoconfigure:4.1.1
org.springframework.boot:spring-boot-autoconfigure:4.1.1
org.springframework.boot:spring-boot-cache:4.1.1
org.springframework.boot:spring-boot-health:4.1.1
org.springframework.boot:spring-boot-http-converter:4.1.1
org.springframework.boot:spring-boot-jackson:4.1.1
org.springframework.boot:spring-boot-jackson2:4.1.1
org.springframework.boot:spring-boot-jersey:4.1.1
org.springframework.boot:spring-boot-micrometer-metrics:4.1.1
org.springframework.boot:spring-boot-micrometer-observation:4.1.1
org.springframework.boot:spring-boot-security:4.1.1
org.springframework.boot:spring-boot-servlet:4.1.1
org.springframework.boot:spring-boot-starter:4.1.1
org.springframework.boot:spring-boot-starter-actuator:4.1.1
org.springframework.boot:spring-boot-starter-cache:4.1.1
org.springframework.boot:spring-boot-starter-jackson:4.1.1
org.springframework.boot:spring-boot-starter-jersey:4.1.1
org.springframework.boot:spring-boot-starter-logging:4.1.1
org.springframework.boot:spring-boot-starter-micrometer-metrics:4.1.1
org.springframework.boot:spring-boot-starter-security:4.1.1
org.springframework.boot:spring-boot-starter-test:4.1.1 (test)
org.springframework.boot:spring-boot-starter-tomcat:4.1.1
org.springframework.boot:spring-boot-starter-tomcat-runtime:4.1.1
org.springframework.boot:spring-boot-starter-validation:4.1.1
org.springframework.boot:spring-boot-starter-web:4.1.1
org.springframework.boot:spring-boot-test:4.1.1 (test)
org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (test)
org.springframework.boot:spring-boot-tomcat:4.1.1
org.springframework.boot:spring-boot-validation:4.1.1
org.springframework.boot:spring-boot-web-server:4.1.1
org.springframework.boot:spring-boot-webmvc:4.1.1
org.springframework.cloud:spring-cloud-commons:5.0.3
org.springframework.cloud:spring-cloud-config-client:5.0.5
org.springframework.cloud:spring-cloud-context:5.0.3
org.springframework.cloud:spring-cloud-starter:5.0.3
org.springframework.data:spring-data-commons:4.1.1 (test)
org.springframework.data:spring-data-jpa:1.5.0.RELEASE (test)
org.springframework.security:spring-security-acl:7.1.1
org.springframework.security:spring-security-config:7.1.1
org.springframework.security:spring-security-core:7.1.1
org.springframework.security:spring-security-crypto:7.1.1
org.springframework.security:spring-security-web:7.1.1
org.springframework:spring-aop:7.0.9
org.springframework:spring-aspects:7.0.9
org.springframework:spring-beans:7.0.9
org.springframework:spring-context:7.0.9
org.springframework:spring-context-support:7.0.9
org.springframework:spring-core:7.0.9
org.springframework:spring-expression:7.0.9
org.springframework:spring-instrument:7.0.9
org.springframework:spring-jdbc:7.0.9
org.springframework:spring-orm:7.0.9
org.springframework:spring-test:7.0.9 (test)
org.springframework:spring-tx:7.0.9
org.springframework:spring-web:7.0.9
org.springframework:spring-webmvc:7.0.9
org.subethamail:subethasmtp-smtp:1.2 (test)
org.subethamail:subethasmtp-wiser:1.2 (test)
org.tuckey:urlrewritefilter:4.0.4
org.w3c.css:sac:1.3 (test)
org.webbitserver:webbit:0.4.14 (test)
org.xmlunit:xmlunit-core:2.11.0 (test)
org.yaml:snakeyaml:2.6
tools.jackson.core:jackson-core:3.1.5
tools.jackson.core:jackson-databind:3.1.5
xalan:serializer:2.7.1 (test)
xalan:xalan:2.7.1 (test)
xerces:xercesImpl:2.11.0 (test)
xml-apis:xml-apis:1.4.01 (test)
```

## vahub (`clns-acuity-vahub`)

**Maven modules:** `vahub-config`, `vahub-common`, `vahub-model`, `vahub-cohorteditor-model`, `vahub`

251 artifacts.
Artifacts resolved to different versions in different modules: `com.github.springtestdbunit:spring-test-dbunit`, `com.google.code.gson:gson`, `com.jayway.jsonpath:json-path`, `commons-codec:commons-codec`, `de.javakaffee:kryo-serializers`, `org.aspectj:aspectjrt`, `org.aspectj:aspectjweaver`, `org.codehaus.janino:janino`, `org.glassfish.jersey.core:jersey-client`, `org.glassfish.jersey.media:jersey-media-json-jackson`, `org.jooq:joor`, `org.objenesis:objenesis`, `org.ow2.asm:asm`, `org.slf4j:slf4j-api`.

```
antlr:antlr:2.7.7
ch.qos.logback:logback-classic:1.5.38
ch.qos.logback:logback-core:1.5.38
com.acuity.visualisations.va-common:va-common-auditlogger:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-auth:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-common:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-config:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-domain:9.0-beryllium-SNAPSHOT
com.acuity.visualisations:vahub-cohorteditor-model:9.0-beryllium-SNAPSHOT
com.acuity.visualisations:vahub-common:9.0-beryllium-SNAPSHOT
com.acuity.visualisations:vahub-common:tests:9.0-beryllium-SNAPSHOT (test)
com.acuity.visualisations:vahub-config:9.0-beryllium-SNAPSHOT
com.acuity.visualisations:vahub-config:tests:9.0-beryllium-SNAPSHOT (test)
com.acuity.visualisations:vahub-model:9.0-beryllium-SNAPSHOT
com.axibase:math:1.0.0
com.esotericsoftware:kryo:4.0.0
com.esotericsoftware:minlog:1.3.0
com.esotericsoftware:reflectasm:1.11.3
com.fasterxml.jackson.core:jackson-annotations:2.21
com.fasterxml.jackson.core:jackson-core:2.21.7
com.fasterxml.jackson.core:jackson-databind:2.21.7
com.fasterxml.jackson.jaxrs:jackson-jaxrs-base:2.21.5
com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.21.5
com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations:2.21.5
com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.21.5
com.fasterxml:classmate:1.7.3
com.github.ben-manes.caffeine:caffeine:3.2.4
com.github.springtestdbunit:spring-test-dbunit:1.1.0,1.3.0 (test)
com.google.code.gson:gson:2.13.2,2.8.0
com.google.errorprone:error_prone_annotations:2.49.0
com.google.guava:guava:21.0
com.google.protobuf:protobuf-java:4.35.1
com.googlecode.concurrent-trees:concurrent-trees:2.6.0
com.googlecode.cqengine:cqengine:2.9.2
com.googlecode.cqengine:cqengine-stream-support:1.1.0
com.h2database:h2:1.4.190 (test)
com.jayway.jsonpath:json-path:2.10.0,2.2.0 (test)
com.jayway.restassured:json-path:2.6.0 (test)
com.jayway.restassured:rest-assured:2.6.0 (test)
com.jayway.restassured:rest-assured-common:2.6.0 (test)
com.jayway.restassured:xml-path:2.6.0 (test)
com.sun.istack:istack-commons-runtime:4.1.2
com.thoughtworks.qdox:qdox:1.12
com.vaadin.external.google:android-json:0.0.20131108.vaadin1 (test)
com.zaxxer:HikariCP:7.0.2
commons-beanutils:commons-beanutils:1.11.0
commons-codec:commons-codec:1.10,1.21.0
commons-collections:commons-collections:3.2.2
commons-digester:commons-digester:2.1
commons-io:commons-io:2.15.1
commons-logging:commons-logging:1.3.6
commons-validator:commons-validator:1.11.0
de.javakaffee:kryo-serializers:0.37,0.42
io.jsonwebtoken:jjwt:0.6.0
io.micrometer:micrometer-commons:1.17.1
io.micrometer:micrometer-core:1.17.1
io.micrometer:micrometer-jakarta9:1.17.1
io.micrometer:micrometer-observation:1.17.1
io.takari.junit:takari-cpsuite:1.2.7
jakarta.activation:jakarta.activation-api:2.1.4
jakarta.annotation:jakarta.annotation-api:3.0.0
jakarta.inject:jakarta.inject-api:2.0.1
jakarta.persistence:jakarta.persistence-api:3.2.0
jakarta.servlet:jakarta.servlet-api:6.1.0 (test)
jakarta.transaction:jakarta.transaction-api:2.0.1
jakarta.validation:jakarta.validation-api:3.1.1
jakarta.ws.rs:jakarta.ws.rs-api:4.0.0
jakarta.xml.bind:jakarta.xml.bind-api:4.0.5
javax.ws.rs:javax.ws.rs-api:2.0.1
javax.xml.bind:jaxb-api:2.2.12
junit:junit:4.13.2
net.arnx:wmf2svg:0.9.11
net.bytebuddy:byte-buddy:1.18.11
net.bytebuddy:byte-buddy-agent:1.18.11 (test)
net.engio:mbassador:1.3.2
net.javacrumbs.json-unit:json-unit-core:1.22.0 (test)
net.javacrumbs.json-unit:json-unit-fluent:1.22.0 (test)
net.jodah:typetools:0.4.7
net.logstash.logback:logstash-logback-encoder:8.0
net.minidev:accessors-smart:2.6.0 (test)
net.minidev:json-smart:2.6.0 (test)
net.sf.ehcache:ehcache:2.10.9.2 (test)
net.sf.supercsv:super-csv:2.4.0
one.util:streamex:0.7.2
org.antlr:antlr-runtime:3.5.3
org.antlr:antlr4-runtime:4.13.2
org.antlr:stringtemplate:3.2.1
org.apache.commons:commons-collections4:4.6.0
org.apache.commons:commons-compress:1.27.1
org.apache.commons:commons-lang3:3.5
org.apache.commons:commons-math3:3.6.1
org.apache.httpcomponents.client5:httpclient5:5.6.4
org.apache.httpcomponents.core5:httpcore5:5.4.3
org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
org.apache.httpcomponents:httpclient:4.5.14
org.apache.httpcomponents:httpcore:4.4.16
org.apache.httpcomponents:httpmime:4.5.1 (test)
org.apache.logging.log4j:log4j-api:2.25.5
org.apache.logging.log4j:log4j-to-slf4j:2.25.5
org.apache.pdfbox:fontbox:3.0.3
org.apache.pdfbox:pdfbox-io:3.0.3
org.apache.tomcat.embed:tomcat-embed-core:11.0.26
org.apache.tomcat.embed:tomcat-embed-el:11.0.26
org.apache.tomcat.embed:tomcat-embed-websocket:11.0.26
org.apache.tomcat:tomcat-jdbc:11.0.24
org.apache.tomcat:tomcat-juli:11.0.24
org.apache.xmlgraphics:xmlgraphics-commons:2.11
org.apiguardian:apiguardian-api:1.1.2 (test)
org.aspectj:aspectjrt:1.8.1,1.9.25.1
org.aspectj:aspectjweaver:1.8.1,1.8.9,1.9.25.1
org.assertj:assertj-core:3.25.3
org.awaitility:awaitility:4.3.0 (test)
org.bitbucket.b_c:jose4j:0.9.7
org.bouncycastle:bcprov-jdk18on:1.85.2
org.ccil.cowan.tagsoup:tagsoup:1.2.1 (test)
org.checkerframework:checker-qual:3.55.1
org.codehaus.groovy:groovy:2.4.4 (test)
org.codehaus.groovy:groovy-json:2.4.4 (test)
org.codehaus.groovy:groovy-xml:2.4.4 (test)
org.codehaus.janino:commons-compiler:3.1.12
org.codehaus.janino:janino:3.0.7,3.1.12
org.dbunit:dbunit:3.5.2 (test)
org.docx4j.org.apache:xalan-interpretive:11.0.0
org.docx4j.org.apache:xalan-serializer:11.0.0
org.docx4j:docx4j-JAXB-ReferenceImpl:11.5.3
org.docx4j:docx4j-core:11.5.3
org.docx4j:docx4j-openxml-objects:11.5.3
org.docx4j:docx4j-openxml-objects-pml:11.5.3
org.docx4j:docx4j-openxml-objects-sml:11.5.3
org.eclipse.angus:angus-activation:2.0.3
org.glassfish.hk2.external:aopalliance-repackaged:2.5.0-b32
org.glassfish.hk2.external:javax.inject:2.5.0-b32
org.glassfish.hk2:hk2-api:2.5.0-b32
org.glassfish.hk2:hk2-locator:2.5.0-b32
org.glassfish.hk2:hk2-utils:2.5.0-b32
org.glassfish.hk2:osgi-resource-locator:3.0.0
org.glassfish.jaxb:jaxb-core:4.0.9
org.glassfish.jaxb:jaxb-runtime:4.0.9
org.glassfish.jaxb:txw2:4.0.9
org.glassfish.jersey.core:jersey-client:2.25.1,4.0.2
org.glassfish.jersey.core:jersey-common:4.0.2
org.glassfish.jersey.ext:jersey-entity-filtering:4.0.2
org.glassfish.jersey.media:jersey-media-json-jackson:2.25.1,4.0.2
org.hamcrest:hamcrest:3.0
org.hamcrest:hamcrest-core:3.0
org.hamcrest:hamcrest-library:3.0 (test)
org.hdrhistogram:HdrHistogram:2.2.2
org.hibernate.models:hibernate-models:1.1.1
org.hibernate.orm:hibernate-core:7.4.5.Final
org.hibernate.validator:hibernate-validator:9.1.3.Final
org.javassist:javassist:3.18.2-GA
org.jboss.logging:jboss-logging:3.6.3.Final
org.jooq:joor:0.9.4,0.9.6
org.jspecify:jspecify:1.0.1
org.junit.jupiter:junit-jupiter:6.0.3 (test)
org.junit.jupiter:junit-jupiter-api:6.0.3 (test)
org.junit.jupiter:junit-jupiter-engine:6.0.3 (test)
org.junit.jupiter:junit-jupiter-params:6.0.3 (test)
org.junit.platform:junit-platform-commons:6.0.3 (test)
org.junit.platform:junit-platform-engine:6.0.3 (test)
org.junit.platform:junit-platform-launcher:6.0.3 (test)
org.junit.vintage:junit-vintage-engine:6.0.3 (test)
org.mockito:mockito-core:5.23.0 (test)
org.mockito:mockito-junit-jupiter:5.23.0 (test)
org.mybatis:mybatis:3.5.16
org.mybatis:mybatis-spring:3.0.5
org.objenesis:objenesis:2.2,3.3
org.opentest4j:opentest4j:1.3.0 (test)
org.ow2.asm:asm:5.0.4,9.7.1
org.plutext:jaxb-svg11:11.4.0
org.postgresql:postgresql:42.7.13
org.projectlombok:lombok:1.18.46
org.skyscreamer:jsonassert:1.5.3 (test)
org.slf4j:jcl-over-slf4j:2.0.18
org.slf4j:jul-to-slf4j:2.0.18
org.slf4j:log4j-over-slf4j:2.0.18
org.slf4j:slf4j-api:1.7.22,2.0.18
org.springframework.boot:spring-boot:4.1.1
org.springframework.boot:spring-boot-actuator:4.1.1
org.springframework.boot:spring-boot-actuator-autoconfigure:4.1.1
org.springframework.boot:spring-boot-autoconfigure:4.1.1
org.springframework.boot:spring-boot-cache:4.1.1
org.springframework.boot:spring-boot-data-commons:4.1.1
org.springframework.boot:spring-boot-data-jpa:4.1.1
org.springframework.boot:spring-boot-health:4.1.1
org.springframework.boot:spring-boot-hibernate:4.1.1
org.springframework.boot:spring-boot-http-converter:4.1.1
org.springframework.boot:spring-boot-jackson:4.1.1
org.springframework.boot:spring-boot-jdbc:4.1.1
org.springframework.boot:spring-boot-jpa:4.1.1
org.springframework.boot:spring-boot-micrometer-metrics:4.1.1
org.springframework.boot:spring-boot-micrometer-observation:4.1.1
org.springframework.boot:spring-boot-persistence:4.1.1
org.springframework.boot:spring-boot-security:4.1.1
org.springframework.boot:spring-boot-servlet:4.1.1
org.springframework.boot:spring-boot-sql:4.1.1
org.springframework.boot:spring-boot-starter:4.1.1
org.springframework.boot:spring-boot-starter-actuator:4.1.1
org.springframework.boot:spring-boot-starter-cache:4.1.1
org.springframework.boot:spring-boot-starter-data-jpa:4.1.1
org.springframework.boot:spring-boot-starter-jackson:4.1.1
org.springframework.boot:spring-boot-starter-jdbc:4.1.1
org.springframework.boot:spring-boot-starter-logging:4.1.1
org.springframework.boot:spring-boot-starter-micrometer-metrics:4.1.1
org.springframework.boot:spring-boot-starter-security:4.1.1
org.springframework.boot:spring-boot-starter-test:4.1.1 (test)
org.springframework.boot:spring-boot-starter-tomcat:4.1.1
org.springframework.boot:spring-boot-starter-tomcat-runtime:4.1.1
org.springframework.boot:spring-boot-starter-validation:4.1.1
org.springframework.boot:spring-boot-starter-web:4.1.1
org.springframework.boot:spring-boot-test:4.1.1 (test)
org.springframework.boot:spring-boot-test-autoconfigure:4.1.1 (test)
org.springframework.boot:spring-boot-tomcat:4.1.1
org.springframework.boot:spring-boot-transaction:4.1.1
org.springframework.boot:spring-boot-validation:4.1.1
org.springframework.boot:spring-boot-web-server:4.1.1
org.springframework.boot:spring-boot-webmvc:4.1.1
org.springframework.boot:spring-boot-webmvc-test:4.1.1 (test)
org.springframework.cloud:spring-cloud-commons:5.0.3
org.springframework.cloud:spring-cloud-config-client:5.0.5
org.springframework.cloud:spring-cloud-context:5.0.3
org.springframework.cloud:spring-cloud-starter:5.0.3
org.springframework.cloud:spring-cloud-starter-config:5.0.5
org.springframework.data:spring-data-commons:4.1.1
org.springframework.data:spring-data-jpa:4.1.1
org.springframework.security:spring-security-acl:7.1.1
org.springframework.security:spring-security-config:7.1.1
org.springframework.security:spring-security-core:7.1.1
org.springframework.security:spring-security-crypto:7.1.1
org.springframework.security:spring-security-web:7.1.1
org.springframework:spring-aop:7.0.9
org.springframework:spring-aspects:7.0.9
org.springframework:spring-beans:7.0.9
org.springframework:spring-context:7.0.9
org.springframework:spring-context-support:7.0.9
org.springframework:spring-core:7.0.9
org.springframework:spring-expression:7.0.9
org.springframework:spring-instrument:7.0.9
org.springframework:spring-jdbc:7.0.9
org.springframework:spring-messaging:7.0.9
org.springframework:spring-orm:7.0.9
org.springframework:spring-test:7.0.9 (test)
org.springframework:spring-tx:7.0.9
org.springframework:spring-web:7.0.9
org.springframework:spring-webmvc:7.0.9
org.xerial:sqlite-jdbc:3.53.2.1
org.xmlunit:xmlunit-core:2.11.0 (test)
org.yaml:snakeyaml:2.6
se.sawano.java:alphanumeric-comparator:1.4.1
tools.jackson.core:jackson-core:3.1.5
tools.jackson.core:jackson-databind:3.1.5
```

## admin (`clns-acuity-admin`)

**Maven modules:** `acuity-core`, `acuity-etl`, `acuity-common`, `etl-api`

196 artifacts.
Artifacts resolved to different versions in different modules: `commons-logging:commons-logging`, `org.apache.commons:commons-lang3`.

```
ch.qos.logback:logback-classic:1.5.38
ch.qos.logback:logback-core:1.5.38
com.acuity.visualisations.va-security:va-security-auth:9.0-beryllium-SNAPSHOT
com.acuity.visualisations.va-security:va-security-domain:9.0-beryllium-SNAPSHOT
com.acuity.visualisations:adminui-common:9.0-beryllium-SNAPSHOT
com.acuity.visualisations:adminui-etl:9.0-beryllium-SNAPSHOT
com.acuity.visualisations:etl-api:9.0-beryllium-SNAPSHOT
com.epam:parso:2.0.9
com.fasterxml.jackson.core:jackson-annotations:2.21
com.fasterxml.jackson.core:jackson-core:2.21.5
com.fasterxml.jackson.core:jackson-databind:2.21.5
com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.5
com.fasterxml.jackson.jaxrs:jackson-jaxrs-base:2.21.5
com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.21.5
com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.21.5
com.fasterxml:classmate:1.7.3
com.github.ben-manes.caffeine:caffeine:3.2.4
com.google.errorprone:error_prone_annotations:2.49.0
com.google.guava:guava:21.0
com.googlecode.flyway:flyway-core:2.2.1
com.h2database:h2:1.4.190 (test)
com.jayway.jsonpath:json-path:2.10.0
com.microsoft.azure:azure-keyvault-core:0.8.0
com.microsoft.azure:azure-storage:5.3.1
com.sun.istack:istack-commons-runtime:4.1.2
com.vaadin.external.google:android-json:0.0.20131108.vaadin1
com.zaxxer:HikariCP:7.0.2
commons-beanutils:commons-beanutils:1.11.0
commons-codec:commons-codec:1.21.0
commons-collections:commons-collections:3.2.2
commons-digester:commons-digester:2.1
commons-io:commons-io:2.15.1
commons-lang:commons-lang:2.6
commons-logging:commons-logging:1.2,1.3.6
commons-validator:commons-validator:1.11.0
io.micrometer:micrometer-commons:1.17.1
io.micrometer:micrometer-core:1.17.1
io.micrometer:micrometer-observation:1.17.1
jakarta.activation:jakarta.activation-api:2.1.4
jakarta.annotation:jakarta.annotation-api:2.1.1
jakarta.el:jakarta.el-api:5.0.0
jakarta.inject:jakarta.inject-api:2.0.1
jakarta.mail:jakarta.mail-api:2.1.5
jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.2
jakarta.servlet:jakarta.servlet-api:6.0.0
jakarta.validation:jakarta.validation-api:3.1.1
jakarta.ws.rs:jakarta.ws.rs-api:4.0.0
jakarta.xml.bind:jakarta.xml.bind-api:4.0.2
javax.xml.bind:jaxb-api:2.2.12
joda-time:joda-time:2.3
junit:junit:4.12 (test)
net.bytebuddy:byte-buddy:1.18.11
net.bytebuddy:byte-buddy-agent:1.18.11
net.minidev:accessors-smart:2.6.0
net.minidev:json-smart:2.6.0
net.sf.opencsv:opencsv:2.3
net.sourceforge.jtds:jtds:1.2.6
org.apache.commons:commons-collections4:4.6.0
org.apache.commons:commons-fileupload2-core:2.0.0-M5
org.apache.commons:commons-fileupload2-javax:2.0.0-M5
org.apache.commons:commons-lang3:3.20.0,3.5
org.apache.httpcomponents.client5:httpclient5:5.6.4
org.apache.httpcomponents.core5:httpcore5:5.4.3
org.apache.httpcomponents.core5:httpcore5-h2:5.4.3
org.apache.httpcomponents:httpclient:4.5.14
org.apache.httpcomponents:httpcore:4.4.16
org.apache.logging.log4j:log4j-api:2.25.5
org.apache.logging.log4j:log4j-to-slf4j:2.25.5
org.apache.tomcat.embed:tomcat-embed-core:11.0.26
org.apache.tomcat.embed:tomcat-embed-el:11.0.26
org.apache.tomcat.embed:tomcat-embed-jasper:11.0.24
org.apache.tomcat.embed:tomcat-embed-websocket:11.0.26
org.apache.tomcat:tomcat-annotations-api:11.0.24
org.apache.tomcat:tomcat-jdbc:8.0.36
org.apache.tomcat:tomcat-juli:8.0.36
org.apiguardian:apiguardian-api:1.1.2
org.aspectj:aspectjweaver:1.8.9
org.assertj:assertj-core:3.27.7
org.awaitility:awaitility:4.3.0
org.bitbucket.b_c:jose4j:0.9.7
org.bouncycastle:bcprov-jdk18on:1.85.2
org.checkerframework:checker-qual:3.55.1
org.codehaus.janino:commons-compiler:3.1.12
org.codehaus.janino:janino:3.1.12
org.codelibs:jcifs:1.3.18.2
org.dbunit:dbunit:3.5.2 (test)
org.easytesting:fest-assert:1.4 (test)
org.easytesting:fest-util:1.1.6 (test)
org.eclipse.angus:angus-activation:2.0.3
org.eclipse.angus:angus-mail:2.0.5
org.eclipse.jdt:ecj:3.45.0
org.eobjects.metamodel:MetaModel-core:3.1.1 (test)
org.eobjects.metamodel:MetaModel-csv:3.1.1 (test)
org.glassfish.hk2:osgi-resource-locator:3.0.0
org.glassfish.jaxb:jaxb-core:4.0.9
org.glassfish.jaxb:jaxb-runtime:4.0.9
org.glassfish.jaxb:txw2:4.0.9
org.glassfish.jersey.core:jersey-client:4.0.2
org.glassfish.jersey.core:jersey-common:4.0.2
org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1
org.hamcrest:hamcrest:3.0
org.hamcrest:hamcrest-core:3.0 (test)
org.hdrhistogram:HdrHistogram:2.2.2
org.hibernate.validator:hibernate-validator:9.1.3.Final
org.hsqldb:hsqldb:2.2.8 (test)
org.jboss.logging:jboss-logging:3.6.3.Final
org.jetbrains.kotlin:kotlin-stdlib:2.3.21
org.jetbrains:annotations:13.0
org.json:json:20160810
org.jsoup:jsoup:1.9.2
org.jspecify:jspecify:1.0.1
org.junit.jupiter:junit-jupiter:6.0.3
org.junit.jupiter:junit-jupiter-api:6.0.3 (test)
org.junit.jupiter:junit-jupiter-engine:6.0.3 (test)
org.junit.jupiter:junit-jupiter-params:6.0.3
org.junit.platform:junit-platform-commons:6.0.3 (test)
org.junit.platform:junit-platform-engine:6.0.3 (test)
org.junit.platform:junit-platform-launcher:6.0.3 (test)
org.junit.vintage:junit-vintage-engine:6.0.3 (test)
org.mapdb:mapdb:1.0.6
org.mitre.dsmiley.httpproxy:smiley-http-proxy-servlet:1.4
org.mockito:mockito-core:5.23.0
org.mockito:mockito-junit-jupiter:5.23.0 (test)
org.mybatis:mybatis:3.5.16
org.mybatis:mybatis-spring:3.0.5
org.objenesis:objenesis:3.3
org.opentest4j:opentest4j:1.3.0 (test)
org.ow2.asm:asm:9.7.1
org.postgresql:postgresql:42.7.13
org.projectlombok:lombok:1.18.46
org.quartz-scheduler:quartz:2.5.2
org.skyscreamer:jsonassert:1.5.3
org.slf4j:jul-to-slf4j:2.0.18
org.slf4j:log4j-over-slf4j:2.0.18
org.slf4j:slf4j-api:2.0.18
org.springframework.batch:spring-batch-core:6.0.5
org.springframework.batch:spring-batch-infrastructure:6.0.5
org.springframework.batch:spring-batch-test:6.0.5 (test)
org.springframework.boot:spring-boot:4.1.1
org.springframework.boot:spring-boot-autoconfigure:4.1.1
org.springframework.boot:spring-boot-cache:4.1.1
org.springframework.boot:spring-boot-http-converter:4.1.1
org.springframework.boot:spring-boot-jackson:4.1.1
org.springframework.boot:spring-boot-jdbc:4.1.1
org.springframework.boot:spring-boot-mail:4.1.1
org.springframework.boot:spring-boot-persistence:4.1.1
org.springframework.boot:spring-boot-security:4.1.1
org.springframework.boot:spring-boot-servlet:4.1.1
org.springframework.boot:spring-boot-sql:4.1.1
org.springframework.boot:spring-boot-starter:4.1.1
org.springframework.boot:spring-boot-starter-cache:4.1.1
org.springframework.boot:spring-boot-starter-jackson:4.1.1
org.springframework.boot:spring-boot-starter-jdbc:4.1.1
org.springframework.boot:spring-boot-starter-logging:4.1.1
org.springframework.boot:spring-boot-starter-mail:4.1.1
org.springframework.boot:spring-boot-starter-security:4.1.1
org.springframework.boot:spring-boot-starter-test:4.1.1
org.springframework.boot:spring-boot-starter-tomcat:4.1.1
org.springframework.boot:spring-boot-starter-tomcat-runtime:4.1.1
org.springframework.boot:spring-boot-starter-validation:4.1.1
org.springframework.boot:spring-boot-starter-web:4.1.1
org.springframework.boot:spring-boot-test:4.1.1
org.springframework.boot:spring-boot-test-autoconfigure:4.1.1
org.springframework.boot:spring-boot-tomcat:4.1.1
org.springframework.boot:spring-boot-transaction:4.1.1
org.springframework.boot:spring-boot-validation:4.1.1
org.springframework.boot:spring-boot-web-server:4.1.1
org.springframework.boot:spring-boot-webmvc:4.1.1
org.springframework.cloud:spring-cloud-commons:5.0.3
org.springframework.cloud:spring-cloud-config-client:5.0.5
org.springframework.cloud:spring-cloud-context:5.0.3
org.springframework.cloud:spring-cloud-starter:5.0.3
org.springframework.cloud:spring-cloud-starter-config:5.0.5
org.springframework.retry:spring-retry:1.2.0.RELEASE
org.springframework.security:spring-security-acl:7.1.1
org.springframework.security:spring-security-config:7.1.1
org.springframework.security:spring-security-core:7.1.1
org.springframework.security:spring-security-crypto:7.1.1
org.springframework.security:spring-security-taglibs:7.1.1
org.springframework.security:spring-security-web:7.1.1
org.springframework:spring-aop:7.0.9
org.springframework:spring-beans:7.0.9
org.springframework:spring-context:7.0.9
org.springframework:spring-context-support:7.0.9
org.springframework:spring-core:7.0.9
org.springframework:spring-expression:7.0.9
org.springframework:spring-jdbc:7.0.9
org.springframework:spring-test:7.0.9
org.springframework:spring-tx:7.0.9
org.springframework:spring-web:7.0.9
org.springframework:spring-webmvc:7.0.9
org.xmlunit:xmlunit-core:2.11.0
org.yaml:snakeyaml:2.6
pl.pragmatists:JUnitParams:1.0.5 (test)
tools.jackson.core:jackson-core:3.1.5
tools.jackson.core:jackson-databind:3.1.5
```

