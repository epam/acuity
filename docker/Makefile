IMAGES := postgres flyway va-security admin va-hub va-hub-ui

# main = full stack, initdb = just postgres + migrations (see docker-compose.yml profiles)
PROFILE ?= main

# build-% targets are left off .PHONY on purpose: make 3.81 skips pattern rules
# for phony targets. They create no file, so the recipe runs every invocation anyway.
.PHONY: build_all up up_build down down_volumes

# NO_CACHE=1 builds without the docker builder cache
BUILD_FLAGS := $(if $(NO_CACHE),--no-cache,)

build-%:
	docker build $(BUILD_FLAGS) -f images/$*.Dockerfile --tag=acuity-$* --load ..

build_all: $(addprefix build-,$(IMAGES))

up:
	docker compose --profile $(PROFILE) up -d

up_build: build_all
	docker compose --profile $(PROFILE) up -d

down:
	docker compose --profile $(PROFILE) down

down_volumes:
	docker compose --profile $(PROFILE) down -v
