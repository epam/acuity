FROM docker.io/postgres:11-bullseye

WORKDIR /usr/root

RUN apt update \
	&& apt-get install postgresql-11-orafce -y

EXPOSE 5432
