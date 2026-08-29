FROM postgres:17-alpine AS builder

RUN apk add --no-cache \
        git \
        build-base \
        bison \
        flex \
        readline-dev \
        zlib-dev \
        openssl-dev \
        libxml2-dev \
        libxslt-dev \
        icu-dev \
        clang \
        llvm

RUN ln -sf "$(command -v clang)" /usr/bin/clang-21
RUN mkdir -p /usr/lib/llvm21/bin && ln -sf "$(command -v llvm-lto)" /usr/lib/llvm21/bin/llvm-lto
RUN git clone --depth 1 --branch VERSION_4_16_7 https://github.com/orafce/orafce.git /tmp/orafce
RUN cd /tmp/orafce && make USE_PGXS=1 && make USE_PGXS=1 install


FROM postgres:17-alpine

WORKDIR /usr/root

COPY --from=builder /usr/local/lib/postgresql /usr/local/lib/postgresql
COPY --from=builder /usr/local/share/postgresql/extension /usr/local/share/postgresql/extension
COPY clns-acuity-flyway/docker_resources/postgres/create_db.sql /docker-entrypoint-initdb.d/

EXPOSE 5432
