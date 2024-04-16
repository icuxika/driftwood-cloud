FROM openjdk:21-slim
LABEL MAINTAINER=icuxika@outlook.com

ENV TZ=Asia/Shanghai

ENV JAR_FILE=target/gateway.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages -Dcsp.sentinel.app.type=1 -Dcsp.sentinel.log.dir=logs/gateway/sentinel -DJM.LOG.PATH=logs/gateway -DJM.SNAPSHOT.PATH=logs/gateway"
COPY ${JAR_FILE} app.jar
EXPOSE 8900
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar