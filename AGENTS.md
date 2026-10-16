# ACUITY Monorepo

This repo is a multi-project healthcare analytics monorepo. Treat each top-level
`clns-*` directory as an independently buildable app or service, but keep shared
Docker/dev workflows in `docker/` aligned with the backend apps.

## Session Start

- Read `docs/WORKING_MEMORY.md` at the start of work; it stores repo-specific,
  evolving context that should survive between sessions.
- Read `docs/DEPENDENCIES.md` only when the task is about library versions,
  transitive dependencies, or upgrade planning.

## Repo Areas

- `docker/` - local full-stack Docker workflow, compose file, env files,
  Dockerfiles, and stack README.
- `clns-acuity-vahub/` - VAHub backend.
- `clns-acuity-va-security/` - shared security/auth components and services.
- `clns-acuity-admin/` - admin UI/backend and ETL modules.
- `clns-acuity-flyway/` - DB migrations used by local Docker images.
- `infrastructure/` - limited infra code only; do not assume cloud deployment
  manifests live here.

## Local Docker Workflow

- Run stack commands from `docker/`.
- Main local entrypoint is `make up_build`.
- DB-only startup is `make up_build PROFILE=initdb`.
- If apps cannot reach Postgres locally, verify the compose service name and app
  JDBC host defaults stay aligned.

## Persistent Memory Rules

- When you discover repo-level facts that future sessions will need, update
  `docs/WORKING_MEMORY.md` in the same task.
- Keep `docs/WORKING_MEMORY.md` short, factual, and task-relevant; store durable
  findings, not full transcripts.
- Prefer adding links/paths to detailed docs rather than duplicating large
  content.

## Cloud / Runtime Investigations

- The actual cloud deployment topology is not fully represented in this repo.
  If debugging runtime load balancer, replica, or sticky-session behavior,
  expect to need evidence from AWS/ECS/Kubernetes or another infra repo.

## Dependency Inventories

- Full resolved dependency inventories for `va-security`, `vahub`, and `admin`
  live in `docs/DEPENDENCIES.md`.
