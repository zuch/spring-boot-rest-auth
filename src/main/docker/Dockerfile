FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]




# First stage: complete build environment
#FROM maven:3.5.0-jdk-8-alpine AS builder
#
## add pom.xml and source code
#ADD ./pom.xml pom.xml
#ADD ./src src/
#
## package jar
#RUN mvn clean package
#
## Second stage: minimal runtime environment
#From openjdk:8-jre-alpine
#
## copy jar from the first stage
#COPY --from=builder target/my-app-1.0-SNAPSHOT.jar my-app-1.0-SNAPSHOT.jar
#
#EXPOSE 8080
#
#CMD ["java", "-jar", "my-app-1.0-SNAPSHOT.jar"]