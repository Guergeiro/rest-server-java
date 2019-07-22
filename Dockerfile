FROM maven:3.6.1-jdk-11

COPY . /usr/src/
WORKDIR /usr/src/rest-server

RUN mvn clean install
RUN mvn dependency:resolve
RUN mvn verify

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait

EXPOSE 4567
CMD /wait && java -jar ./target/rest-server.jar config.config
