# ACUITY local Docker stack

Runs the full ACUITY stack locally with Docker Compose. All images are built
from this repo; nothing is pulled from a registry.

Run every command from this `docker/` directory.

## Services

Compose project name: `acuity`. Startup order is enforced via healthchecks and
`depends_on`.

| Service | Host port | Description |
|---|---|---|
| `acuity-postgres` | 5432 | PostgreSQL 17 + orafce. Data in the `pgdata` volume. |
| `acuity-flyway-migrate` | – | One-shot. Runs DB migrations, then exits. Waits for postgres healthy. |
| `acuity-va-security` | 8000 | Auth service (WAR). |
| `acuity-admin` | 9090 | Admin UI (WAR). |
| `acuity-va-hub` | 8080 | VAHub backend API (Spring Boot JAR). |
| `acuity-va-hub-ui` | 3000 | VAHub Angular SPA served by nginx, which proxies `/resources/*` to `acuity-va-hub`. |

`va-security`, `admin` and `va-hub` start after `flyway-migrate` completes.
`va-hub-ui` starts after `va-hub`.

## Images

Built by the Makefile, tagged `acuity-<name>`. Dockerfiles live in `images/`.

| Image | Base | Notes |
|---|---|---|
| `acuity-postgres` | `postgres:17-alpine` | Builds the orafce extension from source; seeds via `create_db.sql`. |
| `acuity-flyway` | `flyway/flyway:10-alpine` | Bundles the SQL migrations and `flyway.conf`. |
| `acuity-va-security` | `eclipse-temurin:8-jre` | Maven multi-stage build of `clns-acuity-va-security/web`. |
| `acuity-admin` | `eclipse-temurin:8-jre` | Maven build; installs `va-security` auth module locally first. |
| `acuity-va-hub` | `eclipse-temurin:8-jre` | Maven build; installs `va-security` modules locally first. |
| `acuity-va-hub-ui` | `nginx:1.27-alpine` | 3 stages: generate `.d.ts` (Maven), build Angular bundle (node 6), serve static. |

The Java images build against the monorepo, so build context is the repo root
(`..`).

## Environment variables

One file per service in `env-configs/`. Defaults are for local dev only.

**`postgres.env`**

| Var | Purpose |
|---|---|
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Superuser credentials. |
| `POSTGRES_DB` | Database name (`acuity_db`). |
| `POSTGRES_PORT` | Container port. |
| `PGDATA` | Data directory inside the volume. |
| `POSTGRES_HOST_AUTH_METHOD` / `POSTGRES_INITDB_ARGS` | Forced to `md5` — va-security ships a JDBC driver too old for scram-sha-256. |

**`flyway.env`**

| Var | Purpose |
|---|---|
| `FLYWAY_USER` / `FLYWAY_PASSWORD` | DB credentials Flyway migrates with. |

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

**`vahub-ui.env`**

| Var | Purpose |
|---|---|
| `VAHUB_API` | Backend URL nginx proxies `/resources/*` to (`http://acuity-va-hub:8000`). |

## Build and run

Requires Docker with Compose v2.

| Command | Action |
|---|---|
| `make build_all` | Build all images. |
| `make build-<name>` | Build one image. `<name>` ∈ `postgres flyway va-security admin va-hub va-hub-ui`. |
| `make up` | Start the stack (detached). Images must already be built. |
| `make up_build` | Build all images, then start. |
| `make down` | Stop and remove containers. |
| `make down_volumes` | Same as `down`, plus delete volumes (wipes the DB). |

Add `NO_CACHE=1` to any build target to build without the Docker cache.

First run:

```
make up_build
```
