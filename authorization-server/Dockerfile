FROM openjdk:21-slim
LABEL MAINTAINER=icuxika@outlook.com

ENV TZ=Asia/Shanghai

ENV JAR_FILE=target/authorization-server.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages -Dcsp.sentinel.log.dir=logs/authorization-server/sentinel -DJM.LOG.PATH=logs/authorization-server -DJM.SNAPSHOT.PATH=logs/authorization-server"
ENV DEBUG_JAVA_OPTS=""
COPY ${JAR_FILE} app.jar
EXPOSE 8901
ENTRYPOINT java ${DEBUG_JAVA_OPTS} ${JAVA_OPTS} -jar /app.jar