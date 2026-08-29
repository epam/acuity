FROM maven:3.9.9-eclipse-temurin-8 AS builder

WORKDIR /build
COPY .git ./.git
COPY clns-acuity-va-security ./clns-acuity-va-security

# Checks profile (checkstyle/findbugs) is for CI, not needed for the artifact.
RUN mvn -f clns-acuity-va-security/pom.xml -B -pl web -am -P '!checks' \
        -DskipTests clean package \
        && cp clns-acuity-va-security/web/target/*.war /build/app.war


FROM eclipse-temurin:8-jre

WORKDIR /usr/root
COPY clns-acuity-va-security/web/docker-resources/application.yml /usr/root/local-configs/application.yml
COPY --from=builder /build/app.war /usr/root/app.war

# profiles + JAVA_OPTIONS come from env-configs/va-security.env; :- defaults are for bare `docker run`
EXPOSE 8000
CMD java -Dspring.profiles.active=${ENV_TYPE_PROFILE:-dev},${AUTH_PROFILE:-local-no-security},${CONFIG_PROFILE:-local-config},${OTHER_PROFILES:-default,postgres-mode} \
        -Dserver.port=8000 \
        -Dspring.config.location=./local-configs/ \
        $JAVA_OPTIONS \
        -jar /usr/root/app.war
