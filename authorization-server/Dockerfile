FROM openjdk:17-alpine
LABEL MAINTAINER=icuxika@outlook.com

ENV TZ=Asia/Shanghai

RUN apk add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && apk del tzdata

ENV JAR_FILE=target/authorization-server.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages -Dcsp.sentinel.log.dir=logs/authorization-server/sentinel -DJM.LOG.PATH=logs/authorization-server -DJM.SNAPSHOT.PATH=logs/authorization-server"
COPY ${JAR_FILE} app.jar
EXPOSE 8901
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar