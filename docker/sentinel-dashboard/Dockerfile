FROM openjdk:17-alpine
LABEL MAINTAINER=icuxika@outlook.com

ENV TZ=Asia/Shanghai

RUN apk add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && apk del tzdata

ARG version
ARG username
ARG password
RUN echo "sentinel dashboard version: $version"
RUN echo "sentinel dashboard username: $username"
RUN echo "sentinel dashboard password: $password"
ENV SENTINEL_DASHBOARD_USERNAME=${username}
ENV SENTINEL_DASHBOARD_PASSWORD=${password}

ENV JAR_FILE=sentinel-dashboard-${version}.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages -Xms128m -Xmx256m"
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -Dsentinel.dashboard.auth.username=${SENTINEL_DASHBOARD_USERNAME} -Dsentinel.dashboard.auth.password=${SENTINEL_DASHBOARD_PASSWORD} -jar /app.jar