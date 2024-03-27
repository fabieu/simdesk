FROM openjdk:17-jdk-alpine3.14

WORKDIR /opt/simdesk
RUN mkdir data results

COPY build/libs/simdesk-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /opt/simdesk/app.jar"]
