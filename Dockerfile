FROM adoptopenjdk/openjdk17:alpine
ARG JAR_FILE=build/libs/leaderboard-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
