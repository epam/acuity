# ACUITY local Docker stack

Runs the full ACUITY stack locally with Docker Compose. All images are built
from this repo; nothing is pulled from a registry.

Run every command from this `docker/` directory.

## Services

Compose project name: `acuity`. Startup order is enforced via healthchecks and
`depends_on`.

| Service | Host port | Profile | Description |
|---|---|---|---|
| `acuity-postgres` | 5432 | `main`, `initdb` | PostgreSQL 17 + orafce. Data in the `pgdata` volume. |
| `acuity-flyway-migrate` | – | `main`, `initdb` | One-shot. Runs DB migrations, then exits. Waits for postgres healthy. |
| `acuity-va-security` | 8000 | `main` | Auth service (WAR). |
| `acuity-admin` | 9090 | `main` | Admin UI (WAR). |
| `acuity-va-hub` | 8080 | `main` | VAHub backend API (Spring Boot JAR). |
| `acuity-va-hub-ui` | 3000 | `main` | VAHub Angular SPA served by nginx, which proxies `/resources/*` to `acuity-va-hub`. |

`va-security`, `admin` and `va-hub` start after `flyway-migrate` completes.
`va-hub-ui` starts after `va-hub`.

**Profiles**: `main` is the full stack (default). `initdb` is just
postgres + the migration — useful for setting up the DB alone. Selected via
the Makefile's `PROFILE` var, not passed to `docker compose` directly.

## Images

Built by the Makefile, tagged
`897021975362.dkr.ecr.us-east-1.amazonaws.com/epm-lstr-acuity/acuity-<name>`
(`:latest` for local builds, `:<version>` for releases). Dockerfiles live in
`images/`.

| Image | Base | Notes |
|---|---|---|
| `acuity-postgres` | `postgres:17-alpine` | Builds the orafce extension from source; seeds via `create_db.sql`. |
| `acuity-flyway` | `flyway/flyway:10-alpine` | Bundles the SQL migrations and `env-configs/flyway.conf`; runs the base image's default `flyway migrate`. Not this repo's `clns-acuity-flyway/.../flyway.conf` (that one stays reserved for the manual-CLI workflow in `clns-acuity-flyway/README.md`). |
| `acuity-va-security` | `eclipse-temurin:8-jre` | Maven multi-stage build of `clns-acuity-va-security/web`. |
| `acuity-admin` | `eclipse-temurin:8-jre` | Maven build; installs `va-security` auth module locally first. |
| `acuity-va-hub` | `eclipse-temurin:8-jre` | Maven build; installs `va-security` modules locally first. |
| `acuity-va-hub-ui` | `nginx:1.27-alpine` | 3 stages: generate `.d.ts` (Maven), build Angular bundle (node 6), serve static. |

The Java images build against the monorepo, so build context is the repo root
(`..`).

## Environment variables

- One file per service in `env-configs/`. Defaults are for local dev only.
Every load-bearing value (DB connection strings, usernames, passwords,
inter-service URLs) is overridable via env var with no image rebuild — see
each service's table below.
- `acuity-flyway-migrate` is the exception: its structural settings
(locations/schemas/etc, see `flyway.conf` below) are baked into the image at
build time (not `env_file`) since they're tied to the migration bundle
itself, not the environment. Edit `env-configs/flyway.conf` and re-run
`make build-local-flyway` only if you need to change those.

**`postgres.env`**

| Var | Purpose |
|---|---|
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Superuser credentials. |
| `POSTGRES_DB` | Database name (`acuity_db`). |
| `POSTGRES_PORT` | Container port. |
| `PGDATA` | Data directory inside the volume. |
| `POSTGRES_HOST_AUTH_METHOD` / `POSTGRES_INITDB_ARGS` | Forced to `md5` — va-security ships a JDBC driver too old for scram-sha-256. |

**`flyway.conf`** — baked into the image, structural only (`flyway.locations`,
`flyway.schemas`, `flyway.createSchemas`, `flyway.validateOnMigrate`); nothing
load-bearing lives here. Connection and secrets are env vars set on the
`acuity-flyway-migrate` service in `docker-compose.yml` (override via a
project-level `.env` file or the shell — no rebuild needed):

| Var | Purpose |
|---|---|
| `FLYWAY_URL` / `FLYWAY_USER` / `FLYWAY_PASSWORD` | DB connection Flyway migrates with (native Flyway env vars). |
| `FLYWAY_ACUITY_PASSWORD` | Password migrations assign to the `acuity` app role — must match `POSTGRES_PASSWORD` below. Passed as a CLI arg (`flyway.Dockerfile`'s `CMD`) since Flyway has no env var for dynamic `placeholders.*` keys. |

**`acuity-admin.env`, `va-hub.env`, `va-security.env`** (Spring services)

| Var | Purpose |
|---|---|
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | App DB role — the Flyway-provisioned `acuity` role, not the postgres superuser. |
| `ENV_TYPE_PROFILE` | Spring profile, e.g. `dev`. |
| `AUTH_PROFILE` | Auth profile, e.g. `local-no-security`. |
| `CONFIG_PROFILE` | Config profile, e.g. `local-config`. |
| `STORAGE_PROFILE` | (admin only) e.g. `local-storage`. |
| `OTHER_PROFILES` | (va-hub, va-security) extra profiles, comma-separated. |
| `JAVA_OPTIONS` | Optional extra JVM flags. |
| `POSTGRES_URL` | JDBC connection string. Not in the shipped `.env` files (the `application.yml` default already points at `acuity-postgres`) — add it to point at a different DB. |
| `VASECURITY_URL` / `VASECURITY_USERNAME` / `VASECURITY_PASSWORD` | How this service reaches va-security. Same override note as `POSTGRES_URL`. |
| `VAHUB_URL` / `VAHUB_USERNAME` / `VAHUB_PASSWORD` | How this service reaches va-hub. Same override note as `POSTGRES_URL`. |
| `BASIC_AUTH_USERNAME` / `BASIC_AUTH_PASSWORD` | This service's own in-memory basic-auth user (used when a peer calls in under a non-`local-no-security` `AUTH_PROFILE`). |

**`vahub-ui.env`**

| Var | Purpose |
|---|---|
| `VAHUB_API` | Backend URL nginx proxies `/resources/*` to (`http://acuity-va-hub:8000`). |

## Build and run

Requires Docker with Compose v2 (`docker build` uses buildx for `--load` / `--push`).

### Local

| Command | Action |
|---|---|
| `make build-all` | Build all images, tag `:latest`, load into Docker. |
| `make build-local-<name>` | Build one image the same way. `<name>` ∈ `postgres flyway va-security admin va-hub va-hub-ui`. |
| `make up` | Start the stack (detached). Images must already be built. |
| `make up_build` | `build-all`, then start. |
| `make down` | Stop and remove containers. |
| `make down_volumes` | Same as `down`, plus delete volumes (wipes the DB). |

### Release to ECR

| Command | Action |
|---|---|
| `make ecr-login` | Log Docker in to the ECR registry. `AWS_PROFILE=<name>` overrides the AWS profile (else the ambient env is used). |
| `make build-all-release VERSION=x.y.z` | Build every image except postgres, tag `:x.y.z`, push. `VERSION` is required; run `make ecr-login` first. |
| `make build-release-<name> VERSION=x.y.z` | One image, tagged and pushed the same way. |

Add `NO_CACHE=1` to any build target to build without the Docker cache.
Add `PROFILE=initdb` to `up`/`up_build`/`down`/`down_volumes` to target just
postgres + migrations instead of the full stack (default: `main`).

First run:

```
make up_build
```

DB only:

```
make up_build PROFILE=initdb
```
