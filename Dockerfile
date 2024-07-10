FROM eclipse-temurin:21-alpine

ENV SIMDESK_ACC_RESULTS_FOLDERS=results

WORKDIR /app
RUN mkdir data results
COPY build/libs/simdesk-*.jar simdesk.jar

EXPOSE 8080

ENTRYPOINT ["java", "${JAVA_OPTS}", "-jar", "/app/simdesk.jar"]
