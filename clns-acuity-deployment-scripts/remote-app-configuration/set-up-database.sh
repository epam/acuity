#!/bin/bash
#
# Copyright 2021 The University of Manchester
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cd acuity-docker || (echo "acuity-docker directory not exists" && exit)
printf "Please enter the email-address for global admin of VaSecurity ... "
printf "\n"
read email_address
printf "\n"
docker-compose -f docker-compose.yml --profile init_db up --build -d
echo "Waiting flyway complete..."
docker container wait acuity-docker_flyway_1
rm ./images/postgres/data/add_admin_custom.sql || true
echo "
INSERT INTO acuity.acl_sid VALUES (3, true, '${email_address}');

INSERT INTO acuity.users VALUES ('${email_address}', '${email_address}', 'password', true, NULL);

INSERT INTO acuity.authorities VALUES ('${email_address}', 'DEFAULT_ROLE');

INSERT INTO acuity.group_members VALUES (1, '${email_address}', 1);
INSERT INTO acuity.group_members VALUES (2, '${email_address}', 2);
INSERT INTO acuity.group_members VALUES (3, '${email_address}', 3);

SELECT setval('acl_sid_sequence', (SELECT cast(max(id) as bigint) FROM acuity.acl_sid), true);
SELECT setval('group_members_sequence', (SELECT cast(max(id) as bigint) FROM acuity.group_members), true);
" >> ./images/postgres/data/add_admin_custom.sql
docker exec -it acuity-docker_postgres_1 /bin/bash -c "psql -d acuity_db -U dbadmin -f /usr/root/data/add_admin_custom.sql"
exit
