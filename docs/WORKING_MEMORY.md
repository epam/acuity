# Working Memory

Last updated: 2026-10-16

Durable, repo-specific context for developers working on ACUITY. Keep it
concise: record lasting facts, decisions, gotchas and open risks, not task
history. Link to detailed docs instead of duplicating them.

## Repo Overview

- Monorepo; each top-level `clns-*` directory is an independently buildable
  app/service. Main backend work happens in `clns-acuity-admin/`,
  `clns-acuity-vahub/`, `clns-acuity-va-security/` and `docker/`.
- `clns-acuity-flyway/` holds the DB migrations used by the Docker images.
- `infrastructure/` does not contain the cloud deployment setup; runtime
  topology issues need evidence from AWS/ECS or the infra/deployment repo.
- Full resolved dependency lists: `docs/DEPENDENCIES.md`.

## Tech Stack

- Admin, VAHub, VA-Security and config-server: Spring Boot `4.1.1`, Spring
  Cloud `2025.1.3`, Java 21, PostgreSQL JDBC `42.7.13`.
- Admin uses Spring Batch `6.0.x` (Boot-managed) with Quartz scheduling.
- VA-Security REST uses Jersey `4.0.2` with `jersey-spring6`.
- VAHub is on Jackson 3, with Jackson 2 (`jackson-core`/`jackson-databind`)
  pinned to `2.21.7` in VAHub's parent `dependencyManagement` for libraries
  that still use it.
- VAHub imports `spring-boot-dependencies` as a BOM instead of inheriting the
  Boot parent, so overriding a version property (e.g. `postgresql.version`)
  is not enough; add an explicit `dependencyManagement` entry.

## Local Docker Stack

- Run from `docker/`: `make up_build` (full stack) or
  `make up_build PROFILE=initdb` (DB only).
- Compose service names must match app defaults: `acuity-postgres` (JDBC
  host), `acuity-va-hub` (inter-service calls), `acuity-admin`,
  `acuity-flyway-migrate`.
- Ports: VAHub `http://localhost:8080/`, AdminUI `http://localhost:9090/`.
- Local DB: `docker exec -it acuity-acuity-postgres-1 psql -U dbadmin -d acuity_db`
  (password `test`; app schema `acuity`).
- Builder stages in `admin`/`va-hub`/`va-security` Dockerfiles use a shared
  BuildKit Maven cache (`--mount=type=cache,id=m2`). `NO_CACHE=1` only
  disables layer cache; use `docker builder prune` for a clean dependency
  fetch. `make build-release-%` (buildx `--push`) does not share this cache
  with CI unless `--cache-from`/`--cache-to` is configured.

## Flyway Migrations

- The deployed Flyway image (`docker/images/flyway.Dockerfile`) uses
  `docker/env-configs/flyway.conf`, not
  `clns-acuity-flyway/src/main/resources/flyway.conf`.
- That config has `outOfOrder` off and `validateOnMigrate = false`, so a
  migration numbered below the DB's current version is **skipped silently**.
  Always number new migrations above every version already on `main`.
- `MAP_STUDY_RULE.MSR_VALID` (study "Disclaimer" confirmation) was dropped by
  `V1789007000`; studies are treated as always confirmed, so
  `validateStudyEnabled` needs only `studyCompleted` + enabled file rules.
  `study-setup-finish` no longer takes `confirm` (the UI may still send it).
- Write migrations to be safe to re-run (`IF NOT EXISTS`, forward-only
  `setval`) in case they need renumbering later.
- Deploy/run migrations before starting app images that depend on them, e.g.
  `V1789003000__add_study_comment_to_map_study_rule.sql` (Admin
  `StudyRuleDao` reads `MAP_STUDY_RULE.MSR_STUDY_COMMENT`).

## Admin: Spring Batch 6

- Batch config lives in `acuity-core/src/main/resources/spring/`
  (`applicationContext-dao.xml`, `applicationContext-springbatch.xml`).
- Launching: Quartz `JobLauncherDetails` uses `JobOperator` +
  `JobRepository` + `JobRegistry` (`TaskExecutorJobOperator`); the old
  `JobLauncher`/`JobExplorer` beans are gone. The retry AOP pointcut targets
  `TaskExecutorJobOperator`.
- `MapJobRegistry` auto-registers every `Job` bean in Batch 6. Do not add
  `JobRegistryBeanPostProcessor` or `JobRegistrySmartInitializingSingleton`
  (deprecated; the latter causes `DuplicateJobException`).
- Batch 6 PostgreSQL needs `BATCH_JOB_INSTANCE_SEQ` (legacy schema only had
  `batch_job_seq`), created by
  `V1789005000__add_spring_batch_6_job_instance_sequence.sql`. Symptom when
  missing: jobs stay Queued, logs show `relation "batch_job_instance_seq" does
  not exist`.
- Batch 6 moved classes referenced from XML, e.g.
  `org.springframework.batch.core.job.parameters.DefaultJobParametersValidator`.
- `applicationContext-springbatch.xml` uses the versionless `spring-batch.xsd`.
  The batch XML namespace is deprecated in 6.0 and removed in 7.0: migrate
  `ACUITY_ETL_Job`/`AML_Job` to Java `JobBuilder`/`StepBuilder` config before
  any Batch 7 upgrade.
- `CustomJobExecutionDao` rebuilds `JobExecution`s from SQL for UI status. It
  joins `batch_job_instance` for the job name and fills `etl.project`,
  `etl.study`, `etl.launchingTime` job parameters from selected columns.

## Admin: Other Notes

- Logging: `logback-spring.xml` uses `springProfile` sections and an always-on
  console appender; `logging.file.path: /var/log`. The old Janino
  `<if>/<then>` config produced `No appenders present` and silent exits on
  Logback 1.5. VAHub's `vahub-config` logging follows the same pattern.
- Multipart uses `StandardServletMultipartResolver`; FileUpload dependency is
  `commons-fileupload2-javax`.
- Local profiles (`local-auth`, `local-no-security`): VA-Security auth
  providers are profile-guarded to avoid duplicate `AuthenticationProvider`
  warnings when scanned into admin.
- ETL parsers treat a lone `.` in numeric/date CSV columns as null (used as a
  missing-value placeholder in datasets under `docker/local-file-storage/`).
- ETL writes `RESULT_TEST.TST_DOMAIN`; uniqueness is
  `(tst_date, tst_pat_id, tst_domain)`.
- `RESULT_PATIENT_GROUP` ETL identity includes grouping name and study part,
  so a subject can have multiple independent groupings.

## VAHub Notes

- Subject groupings are read directly from `RESULT_PATIENT_GROUP` and
  `MAP_SUBJECT_GROUP_RULE` (no `MSG_GROUPING_SELECTED` flag). Raw patient
  values win over rule-derived duplicates. Exposed as
  `Subject.subjectGroupings` / `PopulationFilters.subjectGroupings`, keyed by
  grouping name; legacy `doseCohort`/`otherCohort` are no longer populated.
- Kryo 4 needs explicit serializers for `java.util.concurrent.atomic.*`
  (registered in `vahub-model` `DataProviderConfiguration`); otherwise Java
  21 blocks reflective access.
- Jackson 3 may bind enums by display label when `toString()` is overridden
  (e.g. `AeDetailLevel`). If clients send constant names, add a
  `@JsonCreator` accepting both `name()` and display value, keep `@JsonValue`.

## Building And Testing

- Admin: `mvn -f clns-acuity-admin/pom.xml -pl acuity-core -am clean package`.
  Known pre-existing failures: `PatientGroupTest.shouldIncludeGroupingNameInEntityIdentity`,
  `WorkingWithOpenCSV.testCSVFile` (missing test resource).
- VAHub: build with the default profile; `-Pwebapp` fails locally due to a
  Node/npm 18 incompatibility in `frontend-maven-plugin`. `vahub-model` has
  pre-existing failures (1117 run, 26 failures, 22 errors) from test data.
- VA-Security: run with `-P '!checks'`. Running a single module via
  `mvn -f <module>/pom.xml` resolves siblings from `~/.m2`, so first run
  `mvn -f clns-acuity-va-security/pom.xml -pl <module> -am -P '!checks' install -DskipTests`;
  stale snapshot jars cause misleading failures.
- VA-Security REST tests (`mvn -f rest/pom.xml -B -P '!checks' clean test`):
  - Mocks in `mockApplicationContext.xml` are created via
    `MockitoMockFactoryBean` so Spring does not autowire into Mockito 5
    inline mocks. Resources are `@Component`s picked up by component-scan,
    so every resource dependency needs a mock bean there.
  - `AbstractSpringContextJerseyTest` passes one prebuilt context via the
    `contextConfig` property; the Jersey test container builds its own
    `ApplicationHandler`, and `contextConfigLocation` would create a second
    context whose mocks the tests never stub.
- Static analysis (`checks` profile) uses SpotBugs `4.8.6.6`; legacy FindBugs
  crashes on Java 21 bytecode.

## Vulnerability Scanning (Trivy)

- Scan a clean packaged artifact (`trivy rootfs` on extracted WAR/JAR) rather
  than source trees; `trivy fs` overcounts unresolved versions and stale
  `target/` contents.
- Always `clean` before packaging/scanning; stale `target/WEB-INF/lib` and
  stale `~/.m2` snapshot POMs have produced false positives (old Boot 1.5,
  Jackson 2.6/2.8, log4j 1.x, springfox). A clean `install` refreshes them.
- Findings under `va-security/web/src/main/webapp/node_modules/**` come from
  untracked legacy Grunt build output; exclude those paths.
- Avoid explicit old version pins in module POMs; let Boot/parent
  `dependencyManagement` control versions and override centrally.

## Open Items

- Migrate Admin batch jobs off the deprecated Spring Batch XML namespace
  before Batch 7.
