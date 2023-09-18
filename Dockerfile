# First stage: complete build environment
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# add pom.xml and source code
ADD ./pom.xml pom.xml
ADD ./src src/

# package jar
RUN mvn clean package

# Second stage: minimal runtime environment
FROM eclipse-temurin:17-jre-alpine

# copy jar from the first stage
COPY --from=builder target/onboarding-1.0.0-SNAPSHOT.jar application.jar

EXPOSE 8080

CMD ["java", "-jar", "application.jar"]