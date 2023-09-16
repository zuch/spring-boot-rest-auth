#!/bin/sh

#package spring boot app into a JAR
echo -e "\n package spring boot app into a JAR ...\n"
./mvnw clean package -DskipTests

# copy JAR file into src/main/docker
echo -e "\n copy JAR file into src/main/docker ...\n"
cp target/onboarding-1.0.0-SNAPSHOT.jar src/main/docker

# cd to src/main/docker
echo -e "\n cd to src/main/docker ...\n"
cd src/main/docker || exit

# docker-compose down and rmi existing images
echo -e "\n docker-compose down and rmi existing images ...\n"
docker-compose down
docker rmi onboarding-api:latest
docker rmi postgres:latest

# docker-compose up
echo -e "\n docker-compose up ...\n"
docker-compose up