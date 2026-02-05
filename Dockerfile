FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/api.jar app.jar

EXPOSE ${API_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]