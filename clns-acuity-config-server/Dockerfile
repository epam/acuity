FROM docker.io/openjdk:8u282-jdk

VOLUME /usr/root/acuity-spring-configs

ARG WAR_NAME

ENV WAR_NAME $WAR_NAME
ENV CONFIG_PROFILE local-no-security
#setting config server credentials
ENV CFG_SRV_LOGIN acuity
ENV CFG_SRV_PASSWORD ac3tbasic

ENV JAVA_OPTIONS -Xms384m -Xmx384m

WORKDIR /usr/root
COPY ./target/$WAR_NAME /usr/root/

EXPOSE 8888

CMD java $JAVA_OPTIONS -jar /usr/root/$WAR_NAME
