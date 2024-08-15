# Description: Production Dockerfile for the backend service

FROM maven:3.9.8-amazoncorretto-21 as build

WORKDIR /usr/src/app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY ./src ./src

RUN mvn package -DskipTests

FROM openjdk:21

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]