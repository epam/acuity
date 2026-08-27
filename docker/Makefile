.PHONY: build_pg build_flyway build_va_security build_admin build_va_hub build_va_hub_ui build_all up up_build down down_volumes

# NO_CACHE=1 builds without the docker builder cache
BUILD_FLAGS := $(if $(NO_CACHE),--no-cache,)

build_pg:
	docker build $(BUILD_FLAGS) -f images/postgres.Dockerfile --tag=acuity-postgres --load ..

build_flyway:
	docker build $(BUILD_FLAGS) -f images/flyway.Dockerfile --tag=acuity-flyway --load ..

build_va_security:
	docker build $(BUILD_FLAGS) -f images/va-security.Dockerfile --tag=acuity-va-security --load ..

build_admin:
	docker build $(BUILD_FLAGS) -f images/acuity-admin.Dockerfile --tag=acuity-admin --load ..

build_va_hub:
	docker build $(BUILD_FLAGS) -f images/va-hub.Dockerfile --tag=acuity-va-hub --load ..

build_va_hub_ui:
	docker build $(BUILD_FLAGS) -f images/va-hub-ui.Dockerfile --tag=acuity-va-hub-ui --load ..

build_all: build_pg build_flyway build_va_security build_admin build_va_hub build_va_hub_ui

# builds every image, then brings up the full stack for local verification
up:
	docker compose up -d

up_build: build_all
	docker compose up -d

down:
	docker compose down

down_volumes:
	docker compose down -v
