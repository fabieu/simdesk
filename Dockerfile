ARG JVM_TARGET=21
ARG JVM_FROM=eclipse-temurin:${JVM_TARGET}-alpine

FROM ${JVM_FROM} AS builder
WORKDIR /build
COPY . ./
RUN chmod +x gradlew && sh gradlew -DjvmTarget=${JVM_TARGET} --no-build-cache --no-daemon vaadinBuildFrontend bootJar && mv build/libs/* .
RUN java -Djarmode=layertools -jar simdesk.jar extract

FROM $JVM_FROM
WORKDIR /app
COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/application/ ./

WORKDIR /
ENV SIMDESK_ACC_RESULTS_FOLDERS=results
RUN mkdir data results
EXPOSE 8080
ENTRYPOINT ["java", "${JAVA_OPTS}", "-cp", "/app", "org.springframework.boot.loader.launch.JarLauncher"]
