FROM flyway/flyway:10-alpine

COPY clns-acuity-flyway/src/main/resources/flyway /flyway/sql
COPY clns-acuity-flyway/src/main/resources/flyway.conf /flyway/conf/flyway.conf

ENTRYPOINT ["flyway", \
  "-url=jdbc:postgresql://postgres:5432/acuity_db", \
  "-locations=filesystem:/flyway/sql/release-7.0-helium/migrations,filesystem:/flyway/sql/release-8.0-lithium/migrations", \
  "-schemas=acuity,acuity_utils,maintenance_tasks", \
  "-createSchemas=true", \
  "-outOfOrder=true", \
  "-validateOnMigrate=false", \
  "migrate"]
