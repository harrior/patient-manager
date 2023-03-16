# Patient Management Application

This is a client-server web application for managing a list of patients.

## Features

The application provides CRUD operations for working with the patients' database. It allows you to:

- View a list of all patients
- Create new patient entries
- Edit patient information
- Delete patient records
- Validate patient data
- Search and filter patients in the table

## Technologies

- Backend: Clojure, Leiningen, Ring, next.jdbc, PostgreSQL
- Frontend: ClojureScript, Reagent, Re-Frame
- Deployment: Docker, Kubernetes, CircleCI, Git

## Requirements

- Docker
- Java 17
- Leiningen
- Node.js v19.6
- Kubernetes (e.g., Minikube)

## Local Development Setup

1. Install the required dependencies.
2. Run the Docker Compose file from the `Docker` folder: `docker-compose -f Docker/docker-compose-postgres.yml up`
3. Install the project dependencies: `lein deps`
4. Start the backend: `lein run`
5. Start the frontend: `lein run -m shadow.cljs.devtools.cli --npm watch re-frisk-embedded`

## Running the Application

The application reads its settings from environment variables. You can set them locally or in a container. Default values are provided in the `Docker/.env` file.

The application runs on port 8000 by default: `http://localhost:8000/`.

### Local Docker Container Build

1. Build the container locally using `Docker/Dockerfile`.
2. Run the project in a container from a local build: `Docker/docker-compose-build-local.yml`.

### CircleCI

1. Configure CircleCI to use `.circleci`.
2. After the build, the container is uploaded to DockerHub.

### Running the Container from DockerHub

1. Run the project in a container with the DockerHub image: `Docker/docker-compose-build-remote.yml`.

### Kubernetes

Install Minikube and run: `kubectl apply -f kubernetes.yaml`.

## Testing

To run tests, use `lein test`. Integration tests are used, so a database connection is required.

## Highlights

- Patient data is stored in a format compatible with HL7 FHIR.
- Patient data is stored as a JSONB type field in the database.
