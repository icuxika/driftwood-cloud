FROM openjdk:21-slim
LABEL MAINTAINER=icuxika@outlook.com

ENV TZ=Asia/Shanghai

ENV JAR_FILE=target/admin-service.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -XX:+ShowCodeDetailsInExceptionMessages -Dcsp.sentinel.log.dir=logs/admin-service/sentinel -DJM.LOG.PATH=logs/admin-service -DJM.SNAPSHOT.PATH=logs/admin-service"
COPY ${JAR_FILE} app.jar
EXPOSE 8902
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar