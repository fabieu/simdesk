FROM openjdk:17-jdk-alpine3.14

WORKDIR /opt/acc-server-tools
RUN mkdir data results

COPY build/libs/acc-server-tools-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /opt/acc-server-tools/app.jar"]
