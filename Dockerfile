# FROM eclipse-temurin:17-jdk-jammy
FROM ubuntu
RUN apt update -y
RUN apt upgrade -y
RUN apt-get install leiningen -y
RUN ls -la
WORKDIR /app
COPY . /app
RUN lein deps
RUN lein run -m shadow.cljs.devtools.cli release app --source-maps
RUN lein uberjar
COPY ./target/uberjar/patients-0.1.0-standalone.jar .
EXPOSE 8000
CMD java -jar patients-0.1.0-standalone.jar
