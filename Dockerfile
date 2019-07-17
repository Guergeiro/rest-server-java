FROM maven:3.6.1-jdk-11

COPY . /usr/src/
WORKDIR /usr/src/rest-api

RUN mvn clean install
RUN mvn dependency:resolve
RUN mvn verify

EXPOSE 4567
CMD ["java", "-jar", "./target/rest-api-0.1.0-SNAPSHOT-jar-with-dependencies.jar"]
