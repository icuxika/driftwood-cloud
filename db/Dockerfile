FROM mysql:8.0.29
MAINTAINER icuxika@outlook.com

ENV TZ=Asia/Shanghai

COPY ./0-init-database.sql /docker-entrypoint-initdb.d
COPY ./1-init-table-driftwood-cloud.sql /docker-entrypoint-initdb.d
COPY ./2-init-table-driftwood-cloud-nacos.sql /docker-entrypoint-initdb.d
