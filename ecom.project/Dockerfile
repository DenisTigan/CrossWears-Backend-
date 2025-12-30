
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests



# Etapa 2: Rularea aplicației

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/ecom.project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]