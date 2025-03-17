FROM eclipse-temurin:21-alpine-3.21

# Set environment variable for results folder
ENV SIMDESK_ACC_RESULTS_FOLDERS=results

# Set the working directory
WORKDIR /app

# Create necessary directories
RUN mkdir -p data results

# Copy the application JAR file into the container
COPY build/libs/SimDesk.jar /app/simdesk.jar

# Expose the application port
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/simdesk.jar"]