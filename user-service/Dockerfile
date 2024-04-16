FROM openjdk:21-slim
LABEL MAINTAINER=icuxika@outlook.com

ENV TZ=Asia/Shanghai

ENV JAR_FILE=target/user-service.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages -Dcsp.sentinel.log.dir=logs/user-service/sentinel -DJM.LOG.PATH=logs/user-service -DJM.SNAPSHOT.PATH=logs/user-service"
COPY ${JAR_FILE} app.jar
EXPOSE 8904
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar