FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/quiz-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-container:3306/quiz_db?createDatabaseIfNotExist=true
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=chetan@123

ENTRYPOINT ["java","-jar","app.jar"]
