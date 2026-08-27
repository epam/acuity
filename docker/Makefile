IMAGES := postgres flyway va-security admin va-hub va-hub-ui

# build-% targets are left off .PHONY on purpose: make 3.81 skips pattern rules
# for phony targets. They create no file, so the recipe runs every invocation anyway.
.PHONY: build_all up up_build down down_volumes

# NO_CACHE=1 builds without the docker builder cache
BUILD_FLAGS := $(if $(NO_CACHE),--no-cache,)

build-%:
	docker build $(BUILD_FLAGS) -f images/$*.Dockerfile --tag=acuity-$* --load ..

build_all: $(addprefix build-,$(IMAGES))

up:
	docker compose up -d

up_build: build_all
	docker compose up -d

down:
	docker compose down

down_volumes:
	docker compose down -v
