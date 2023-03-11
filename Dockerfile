FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY target/uberjar/patients-0.1.0-standalone.jar .
EXPOSE 8000
CMD java -jar patients-0.1.0-standalone.jar