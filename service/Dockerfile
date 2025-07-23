FROM openjdk:17-jdk-slim

# Install libstdc++ (needed for C++ dependencies)
RUN apt-get update && apt-get install -y libstdc++6

WORKDIR /app
COPY target/service-3.2.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]