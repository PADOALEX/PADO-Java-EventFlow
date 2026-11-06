FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/java-eventflow-1.0.0.jar app.jar
USER 10001
EXPOSE 5080
ENTRYPOINT ["java", "-jar", "app.jar"]
