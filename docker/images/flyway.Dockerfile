FROM flyway/flyway:10-alpine

COPY clns-acuity-flyway/src/main/resources/flyway /flyway/sql
COPY docker/env-configs/flyway.conf /flyway/conf/flyway.conf

# placeholders.* has no FLYWAY_* env var equivalent (Flyway doesn't bind dynamic map
# keys) - pass it as a CLI arg instead, so it stays overridable without a rebuild.
# base image's own ENTRYPOINT is a fixed ["flyway"], so it has to be replaced here too
# for shell interpolation to run at all instead of being passed to flyway as literal args.
ENTRYPOINT ["sh", "-c", "flyway \"-placeholders.user.acuity.password=${FLYWAY_ACUITY_PASSWORD:-test}\" migrate"]
