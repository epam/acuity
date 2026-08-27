FROM maven:3.9.9-eclipse-temurin-8 AS builder

WORKDIR /build
COPY .git ./.git
COPY clns-acuity-va-security ./clns-acuity-va-security

# checks profile (checkstyle/findbugs) is for CI, not needed to produce the artifact
RUN mvn -f clns-acuity-va-security/pom.xml -B -pl web -am -P '!checks' \
        -DskipTests clean package \
    && cp clns-acuity-va-security/web/target/*.war /build/app.war


FROM eclipse-temurin:8-jre

WORKDIR /usr/root
COPY clns-acuity-va-security/web/docker-resources/wait-for-it.sh wait-for-it.sh
COPY clns-acuity-va-security/web/docker-resources/application.yml /usr/root/local-configs/application.yml
COPY --from=builder /build/app.war /usr/root/app.war
RUN chmod +x wait-for-it.sh

# ENV_TYPE_PROFILE/AUTH_PROFILE/CONFIG_PROFILE/OTHER_PROFILES/CFG_SRV_LOGIN/
# CFG_SRV_PASSWORD/JAVA_OPTIONS come from env-configs/va-security.env (docker-compose
# env_file) - defaults below only cover a plain `docker run` with no env file.
EXPOSE 8000
CMD if [ "${CONFIG_PROFILE:-local-config}" != "local-config" ] ; \
        then ./wait-for-it.sh config-server:8888 --timeout=300 --strict -- echo "Starting va-security..." \
                && spring_config_details='-Dspring.cloud.config.username='${CFG_SRV_LOGIN:-acuity}' \
                -Dspring.cloud.config.password='${CFG_SRV_PASSWORD:-r34ctsbas1c}' \
                -Dspring.cloud.config.uri=http://config-server:8888/acuity-spring-configs'; \
        else spring_config_details='-Dspring.config.location=./local-configs/'; \
    fi \
    && mkdir -p /var/log/gc \
    && mkdir -p /var/log/heap_dump \
    && java -Dspring.profiles.active=${ENV_TYPE_PROFILE:-dev},${AUTH_PROFILE:-local-no-security},${CONFIG_PROFILE:-local-config},${OTHER_PROFILES:-default,postgres-mode} \
            -Dserver.port=8000 \
            $spring_config_details \
            $JAVA_OPTIONS \
            -jar /usr/root/app.war
