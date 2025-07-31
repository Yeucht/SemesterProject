FROM openjdk:17-jdk-slim

# Install libstdc++ (needed for C++ dependencies)
RUN apt-get update && apt-get install -y libstdc++6

WORKDIR /app
COPY target/service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]