FROM openjdk:17-alpine
LABEL MAINTAINER=icuxika@outlook.com

ENV TZ=Asia/Shanghai

RUN apk add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && apk del tzdata

ENV JAR_FILE=target/gateway.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages -Dcsp.sentinel.app.type=1 -Dcsp.sentinel.log.dir=logs/gateway/sentinel -DJM.LOG.PATH=logs/gateway -DJM.SNAPSHOT.PATH=logs/gateway"
COPY ${JAR_FILE} app.jar
EXPOSE 8900
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar