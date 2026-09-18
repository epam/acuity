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
        icu-dev

# with_llvm=no skips the LLVM bitcode PGXS would otherwise emit for JIT inlining, so
# no clang/llvm toolchain is needed. Avoids faking the clang-N / llvmN paths pg_config
# reports, which drift every time the base image bumps LLVM.
RUN git clone --depth 1 --branch VERSION_4_16_7 https://github.com/orafce/orafce.git /tmp/orafce \
    && cd /tmp/orafce \
    && make USE_PGXS=1 with_llvm=no \
    && make USE_PGXS=1 with_llvm=no install


FROM postgres:17-alpine

WORKDIR /usr/root

COPY --from=builder /usr/local/lib/postgresql /usr/local/lib/postgresql
COPY --from=builder /usr/local/share/postgresql/extension /usr/local/share/postgresql/extension
COPY clns-acuity-flyway/docker_resources/postgres/create_db.sql /docker-entrypoint-initdb.d/

EXPOSE 5432
