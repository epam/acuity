FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build
COPY .git ./.git
COPY clns-acuity-va-security ./clns-acuity-va-security
COPY clns-acuity-admin ./clns-acuity-admin

# 1. adminui depends on va-security-auth, not published to a reachable registry - install it into the local repo first.
# 2. Checks profile (checkstyle/findbugs) is for CI, not needed for the artifact.
RUN mvn -f clns-acuity-va-security/pom.xml -B -pl domain,common,auditlogger,auth -am \
        -DskipTests -Dfindbugs.skip=true -Dcheckstyle.skip=true install \
        && mvn -f clns-acuity-admin/pom.xml -B -pl acuity-core -am \
        -Dmaven.test.skip=true -Dfindbugs.skip=true -Dcheckstyle.skip=true clean package \
        && cp clns-acuity-admin/acuity-core/target/adminui-*.war /build/app.war


FROM eclipse-temurin:21-jre

WORKDIR /usr/root
COPY clns-acuity-admin/acuity-core/docker-resources/application.yml /usr/root/local-configs/application.yml
COPY --from=builder /build/app.war /usr/root/app.war

# profiles + JAVA_OPTIONS come from env-configs/acuity-admin.env; :- defaults are for bare `docker run`
EXPOSE 8000
CMD java -Dspring.profiles.active=${ENV_TYPE_PROFILE:-dev},${AUTH_PROFILE:-local-no-security},${CONFIG_PROFILE:-local-config},${STORAGE_PROFILE:-local-storage} \
        -Dserver.port=8000 \
        -Dspring.config.location=./local-configs/ \
        $JAVA_OPTIONS \
        -jar /usr/root/app.war
