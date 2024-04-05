FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
RUN mkdir data results

COPY build/libs/simdesk-*.jar simdesk.jar

ENV SIMDESK_ACC_RESULTS_FOLDERS=results

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/simdesk.jar"]
