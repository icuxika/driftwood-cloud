FROM openjdk:17-alpine
MAINTAINER icuxika@outlook.com

ENV TZ=Asia/Shanghai

RUN apk add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && apk del tzdata

ENV JAR_FILE=target/admin-service.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -Xms64m -Xmx256m"
COPY ${JAR_FILE} app.jar
EXPOSE 8902
ENTRYPOINT ["java","${JAVA_OPTS}","-jar","/app.jar"]