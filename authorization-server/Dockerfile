FROM openjdk:17
MAINTAINER icuxika@outlook.com

ENV TZ=Asia/Shanghai

RUN apk add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone \
    && apk del tzdata

ENV JAR_FILE=target/authorization-server-0.0.1.jar
ENV JAVA_OPTS="-Duser.timezone=${TZ} -Xms64m -Xmx256m"
COPY ${JAR_FILE} app.jar
EXPOSE 8901
ENTRYPOINT ["java","${JAVA_OPTS}","-jar","/app.jar"]