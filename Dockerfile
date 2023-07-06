FROM openjdk:17-jdk-alpine
COPY build/libs/leaderboard-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
