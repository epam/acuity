.PHONY: build_pg build_flyway build_all

build_pg:
	docker build -f images/postgres.Dockerfile --tag=acuity-postgres --load ..

build_flyway:
	docker build -f images/flyway.Dockerfile --tag=acuity-flyway --load ..

build_all: build_pg build_flyway
