FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/mortgage-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# Default Spring profile is 'h2'; override at runtime if needed
ENV SPRING_PROFILES_ACTIVE=h2

ENTRYPOINT ["java", "-jar", "app.jar"]