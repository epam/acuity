# ---- stage 1: generate the .d.ts files the Angular build imports ----
FROM maven:3.9.9-eclipse-temurin-21 AS defs

RUN apt-get update && apt-get install -y --no-install-recommends git \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /build
COPY .git ./.git
COPY clns-acuity-va-security ./clns-acuity-va-security
COPY clns-acuity-vahub ./clns-acuity-vahub

# va-security deps aren't in any registry - install locally, then run vahub far
# enough for typescript-generator to emit vahub-model.d.ts / vahub.d.ts
RUN mvn -f clns-acuity-va-security/pom.xml -B -pl domain,common,auditlogger,auth -am \
    -P '!checks' -DskipTests install \
    && mvn -f clns-acuity-vahub/pom.xml -B -pl vahub -am \
    -P '!checks' -DskipTests process-classes


# ---- stage 2: build the Angular bundle ----
# Angular 19 requires node ^18.19.1 || ^20.11.1 || ^22.x
FROM node:22 AS webapp

# repo path depth so webapp's ../../../../ copydefs paths resolve
WORKDIR /build/clns-acuity-vahub/vahub/src/main/webapp

COPY clns-acuity-vahub/vahub/src/main/webapp/package.json clns-acuity-vahub/vahub/src/main/webapp/package-lock.json ./
# --legacy-peer-deps: codelyzer@5 (dead tslint plugin) still peer-pins @angular/compiler <10
RUN npm ci --no-audit --legacy-peer-deps && test -x node_modules/.bin/ng

COPY clns-acuity-vahub/vahub/src/main/webapp/ ./
COPY --from=defs /build/clns-acuity-vahub/vahub-model/target/vahub-model.d.ts /build/clns-acuity-vahub/vahub-model/target/vahub-model.d.ts
COPY --from=defs /build/clns-acuity-vahub/vahub/target/vahub.d.ts /build/clns-acuity-vahub/vahub/target/vahub.d.ts

RUN npm run build


# ---- stage 3: static runtime ----
FROM nginx:1.27-alpine

COPY --from=webapp /build/clns-acuity-vahub/vahub/src/main/webapp/dist /usr/share/nginx/html
COPY clns-acuity-vahub/vahub/docker-resources/nginx.conf.template /etc/nginx/templates/default.conf.template

EXPOSE 8000
