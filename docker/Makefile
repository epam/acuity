IMAGES := postgres flyway va-security admin va-hub va-hub-ui

# main = full stack, initdb = just postgres + migrations (see docker-compose.yml profiles)
PROFILE ?= main

# build-local-% / build-release-% are left off .PHONY on purpose: make 3.81 skips
# pattern rules for phony targets. They create no file, so the recipe runs anyway.
.PHONY: build-all build-all-release require-version up up_build down down_volumes ecr-login

ECR_REGISTRY := 897021975362.dkr.ecr.us-east-1.amazonaws.com
IMAGE_REPO   := $(ECR_REGISTRY)/epm-lstr-acuity/

# release covers every image except postgres (RDS on AWS, nothing to push)
RELEASE_IMAGES := $(filter-out postgres,$(IMAGES))

# AWS_PROFILE=<name> overrides the profile for aws calls (else the ambient env is used)
AWS_PROFILE_FLAG := $(if $(AWS_PROFILE),--profile $(AWS_PROFILE),)

# NO_CACHE=1 builds without the docker builder cache
BUILD_FLAGS := $(if $(NO_CACHE),--no-cache,)

# local image tagged :latest, loaded into docker, never pushed
build-local-%:
	docker build $(BUILD_FLAGS) -f images/$*.Dockerfile --tag=$(IMAGE_REPO)$*:latest --load ..

build-all: $(addprefix build-local-,$(IMAGES))

# image tagged :$(VERSION), pushed to ECR -- run `make ecr-login` first
build-release-%: require-version
	docker buildx build $(BUILD_FLAGS) -f images/$*.Dockerfile --platform=linux/amd64 --tag=$(IMAGE_REPO)$*:$(VERSION) --push ..

build-all-release: $(addprefix build-release-,$(RELEASE_IMAGES))

require-version:
	@test -n "$(VERSION)" || { echo "VERSION is required, e.g. make build-all-release VERSION=1.2.3" >&2; exit 1; }

ecr-login:
	aws $(AWS_PROFILE_FLAG) ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $(ECR_REGISTRY)

up:
	docker compose --profile $(PROFILE) up -d

up_build: build-all
	docker compose --profile $(PROFILE) up -d

down:
	docker compose --profile $(PROFILE) down

down_volumes:
	docker compose --profile $(PROFILE) down -v
