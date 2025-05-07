# Use a lightweight OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR into the container
COPY target/service-3.2.0.jar app.jar

# Expose port (update if your app uses a different one)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
