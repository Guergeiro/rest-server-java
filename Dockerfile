FROM openjdk:11

# Install maven
RUN apt-get update
RUN apt-get install -y maven

COPY . /usr/src/
WORKDIR /usr/src/rest-api

RUN mvn clean install
RUN mvn dependency:resolve
RUN mvn verify

EXPOSE 4567
CMD ["java", "-jar", "./target/rest-api-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]
