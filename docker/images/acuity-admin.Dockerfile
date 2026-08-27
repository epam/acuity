FROM maven:3.9.9-eclipse-temurin-8 AS builder

WORKDIR /build
COPY .git ./.git
COPY clns-acuity-va-security ./clns-acuity-va-security
COPY clns-acuity-admin ./clns-acuity-admin

# adminui depends on va-security-auth, which isn't published to a reachable registry
# (same monorepo, no GHCR access) - install it into the local repo first.
# checks profile (checkstyle/findbugs) is for CI, not needed to produce the artifact
RUN mvn -f clns-acuity-va-security/pom.xml -B -pl auth -am -P '!checks' \
        -DskipTests install \
    && mvn -f clns-acuity-admin/pom.xml -B -pl acuity-core -am -P '!checks' \
        -DskipTests clean package \
    && cp clns-acuity-admin/acuity-core/target/*.war /build/app.war


FROM eclipse-temurin:8-jre

WORKDIR /usr/root
COPY clns-acuity-admin/acuity-core/docker-resources/wait-for-it.sh wait-for-it.sh
COPY clns-acuity-admin/acuity-core/docker-resources/application.yml /usr/root/local-configs/application.yml
COPY --from=builder /build/app.war /usr/root/app.war
RUN chmod +x wait-for-it.sh

# ENV_TYPE_PROFILE/AUTH_PROFILE/CONFIG_PROFILE/STORAGE_PROFILE/CFG_SRV_LOGIN/
# CFG_SRV_PASSWORD/JAVA_OPTIONS come from env-configs/acuity-admin.env (docker-compose
# env_file) - defaults below only cover a plain `docker run` with no env file.
EXPOSE 8000
CMD if [ "${CONFIG_PROFILE:-local-config}" != "local-config" ] ; \
        then ./wait-for-it.sh config-server:8888 --timeout=300 --strict -- echo "Starting adminui..." \
                && spring_config_details='-Dspring.cloud.config.username='${CFG_SRV_LOGIN:-acuity}' \
                -Dspring.cloud.config.password='${CFG_SRV_PASSWORD:-ac3tbasic}' \
                -Dspring.cloud.config.uri=http://config-server:8888/acuity-spring-configs'; \
        else spring_config_details='-Dspring.config.location=./local-configs/'; \
    fi \
    && mkdir -p /var/log/gc \
    && mkdir -p /var/log/heap_dump \
    && java -Dspring.profiles.active=${ENV_TYPE_PROFILE:-dev},${AUTH_PROFILE:-local-no-security},${CONFIG_PROFILE:-local-config},${STORAGE_PROFILE:-local-storage} \
            -Dserver.port=8000 \
            $spring_config_details \
            $JAVA_OPTIONS \
            -jar /usr/root/app.war
