FROM flyway/flyway:10-alpine

COPY clns-acuity-flyway/src/main/resources/flyway /flyway/sql
COPY docker/env-configs/flyway.conf /flyway/conf/flyway.conf

CMD ["migrate"]
