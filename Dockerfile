#étape 1 : Build

FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests
#étape 2 : Générer jar

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080

#Etape 3 : Exécuter
CMD ["java", "-jar", "app.jar"]