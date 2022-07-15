FROM openjdk:17-alpine
MAINTAINER icuxika@outlook.com

ENV TZ=Asia/Shanghai

RUN apk add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && apk del tzdata

ENV JAR_FILE=target/framework-service-flowable.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages"
COPY ${JAR_FILE} app.jar
EXPOSE 8906
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar