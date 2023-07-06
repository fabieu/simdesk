FROM openjdk:17-jdk-alpine

WORKDIR /opt/acc-leaderboard
RUN mkdir results # default results folder

COPY build/libs/leaderboard-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /opt/acc-leaderboard/app.jar"]
