# FROM eclipse-temurin:17-jdk-jammy
FROM ubuntu

RUN apt update -y
RUN apt upgrade -y

WORKDIR /app
COPY . .

RUN apt install -y curl
RUN curl -fsSL https://deb.nodesource.com/setup_19.x | bash -
RUN apt install -y nodejs
RUN node --version
RUN npm install

RUN apt install leiningen -y
RUN lein deps
RUN lein run -m shadow.cljs.devtools.cli release app --source-maps
RUN lein uberjar
EXPOSE 8000
CMD java -jar ./target/uberjar/patients-0.1.0-standalone.jar
