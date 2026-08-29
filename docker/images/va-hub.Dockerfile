FROM maven:3.9.9-eclipse-temurin-8 AS builder

# vahub's `git-commit-id-plugin` walks up to the monorepo .git - needs the git binary as a dependency.
RUN apt-get update && apt-get install -y --no-install-recommends git \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /build
COPY .git ./.git
COPY clns-acuity-va-security ./clns-acuity-va-security
COPY clns-acuity-vahub ./clns-acuity-vahub

# vahub depends on va-security domain/common/auth + va-common-auditlogger, not
# published to a reachable registry - install them into the local repo first. checks profile is CI-only.
RUN mvn -f clns-acuity-va-security/pom.xml -B -pl domain,common,auditlogger,auth -am \
    -P '!checks' -DskipTests install \
    && mvn -f clns-acuity-vahub/pom.xml -B -pl vahub -am -P '!checks' \
    -DskipTests clean package \
    && cp clns-acuity-vahub/vahub/target/vahub-*.jar /build/app.jar


FROM eclipse-temurin:8-jre

WORKDIR /usr/root
COPY clns-acuity-vahub/vahub/docker-resources/application.yml /usr/root/local-configs/application.yml
COPY --from=builder /build/app.jar /usr/root/app.jar

# profiles + JAVA_OPTIONS come from env-configs/va-hub.env; :- defaults are for bare `docker run`
EXPOSE 8000
CMD java -Dspring.profiles.active=${ENV_TYPE_PROFILE:-dev},${AUTH_PROFILE:-local-no-security},${CONFIG_PROFILE:-local-config},${OTHER_PROFILES:-NoScheduledJobs} \
    -Dserver.port=8000 \
    -Dspring.config.location=./local-configs/ \
    $JAVA_OPTIONS \
    -jar /usr/root/app.jar
