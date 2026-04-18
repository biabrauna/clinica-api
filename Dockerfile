FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:16-jdk-slim
WORKDIR /app
COPY --from=build /app/target/clinica-api-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
