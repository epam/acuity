FROM maven:3.9.9-eclipse-temurin-8 AS builder

# vahub's git-commit-id-plugin runs with useNativeGit=true and walks up from the module
# dir to find .git (monorepo root) - needs the native git binary present to do that.
RUN apt-get update && apt-get install -y --no-install-recommends git \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /build
COPY .git ./.git
COPY clns-acuity-va-security ./clns-acuity-va-security
COPY clns-acuity-vahub ./clns-acuity-vahub

# vahub depends on va-security-domain/common/auth and va-common-auditlogger, none of
# which are published to a reachable registry (same monorepo, no GHCR access) -
# install them into the local repo first.
# checks profile (checkstyle/findbugs) is for CI, not needed to produce the artifact.
# default (jar) profile is used - no "webapp" profile, so no frontend/npm build.
RUN mvn -f clns-acuity-va-security/pom.xml -B -pl domain,common,auditlogger,auth -am \
        -P '!checks' -DskipTests install \
    && mvn -f clns-acuity-vahub/pom.xml -B -pl vahub -am -P '!checks' \
        -DskipTests clean package \
    && cp clns-acuity-vahub/vahub/target/vahub-*.jar /build/app.jar


FROM eclipse-temurin:8-jre

WORKDIR /usr/root
COPY clns-acuity-vahub/vahub/docker-resources/wait-for-it.sh wait-for-it.sh
COPY clns-acuity-vahub/vahub/docker-resources/application.yml /usr/root/local-configs/application.yml
COPY --from=builder /build/app.jar /usr/root/app.jar
RUN chmod +x wait-for-it.sh

# ENV_TYPE_PROFILE/AUTH_PROFILE/CONFIG_PROFILE/OTHER_PROFILES/CFG_SRV_LOGIN/
# CFG_SRV_PASSWORD/JAVA_OPTIONS come from env-configs/va-hub.env (docker-compose
# env_file) - defaults below only cover a plain `docker run` with no env file.
EXPOSE 8000
CMD if [ "${CONFIG_PROFILE:-local-config}" != "local-config" ] ; \
        then ./wait-for-it.sh config-server:8888 --timeout=300 --strict -- echo "Starting vahub..." \
                && spring_config_details='-Dspring.cloud.config.username='${CFG_SRV_LOGIN:-acuity}' \
                -Dspring.cloud.config.password='${CFG_SRV_PASSWORD:-ac3tbasic}' \
                -Dspring.cloud.config.uri=http://config-server:8888/acuity-spring-configs'; \
        else spring_config_details='-Dspring.config.location=./local-configs/'; \
    fi \
    && mkdir -p /var/log/gc \
    && mkdir -p /var/log/heap_dump \
    && java -Dspring.profiles.active=${ENV_TYPE_PROFILE:-dev},${AUTH_PROFILE:-local-no-security},${CONFIG_PROFILE:-local-config},${OTHER_PROFILES:-NoScheduledJobs} \
            -Dserver.port=8000 \
            $spring_config_details \
            $JAVA_OPTIONS \
            -jar /usr/root/app.jar
