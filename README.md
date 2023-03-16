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

Run the project in a container with the DockerHub image: `Docker/docker-compose-build-remote.yml`.

### Kubernetes

Install Minikube and run: `kubectl apply -f kubernetes.yaml`.

## Testing

To run tests, use `lein test`. Integration tests are used, so a database connection is required.

## Highlights

- Patient data is stored in a format compatible with HL7 FHIR.
- Patient data is stored as a JSONB type field in the database.

## Project Structure

This section provides an overview of the project structure, explaining the purpose of key files and folders.

```
├── Docker                     # Docker configuration files
│
├── resources                  # Application resources
│   ├── migrations             # Database migration files
│   └── public                 # Static files for frontend
│       ├── css                # CSS styles
│       └── js                 # JavaScript files
│
├── src                        # Source code directory
│   ├── clj                    # Backend Clojure source code
│   │   └── patients           # Backend application modules
│   └── cljs                   # Frontend ClojureScript source code
│       └── patients           # Frontend application modules
│           ├── components      # Reusable components
│           │   └── table       # Table component
│           └── pages           # Application pages
│               └── patient     # Patient page
│
├── test                       # Test code directory
│   ├── clj                    # Backend Clojure tests
│   └── cljs                   # Frontend ClojureScript tests
│
├── deployment.yaml            # Kubernetes deployment configuration
├── package.json               # Node.js dependencies
├── project.clj                # Clojure project configuration
└── shadow-cljs.edn            # ClojureScript build configuration
```

## API Documentation

This section provides documentation for the API, which uses the RPC (Remote Procedure Call) approach and the EDN (Extensible Data Notation) format for data exchange. The documentation includes available methods, request and response formats, and expected behavior.

### Overview

The API provides CRUD operations for managing patient data in the database. It allows users to create, read, update, and delete patient records. The API has a single endpoint: `/rpc`.

### Request and Response Formats, Status Codes, and Errors
#### Request Format
The request should be formatted as follows:

```
{:method <method-name> :params {<parameter-key> <parameter-value> ...}}
```

- <method-name>: The name of the method to be called (as a keyword).
- <parameter-key>: The parameter key (as a keyword).
- <parameter-value>: The corresponding value for the parameter.

#### Example Request

```
{:method :get-patient :params {:patient-identifier "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6"}}
```

#### Response Format
The response will be formatted as follows:

```
{:status <status-code> :data {<data-key> <data-value> ...}}
```

- <status-code>: status code for the response (as a keyword).
- <data-key>: The data key (as a keyword).
- <data-value>: The corresponding value for the data key.

#### Example Response

```
{:status :ok :data {:patient {:identifier "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6" :name "John Doe" ...}}}
```

### Status Codes
The following status codes are used in the API responses:

- `:ok`: The operation was successful.
- `:error`: There was an error during the operation.

### HTTP Status Codes
- `200 OK`: The request was successful, and the response contains the requested data.
- `400 Bad Request`: The request was malformed or invalid. This status code is used for validation errors, such as issues with the provided parameters.
- `500 Internal Server Error`: A server-side error occurred while processing the request. This status code is used for all other errors that are not related to validation issues.

### Errors
Errors can occur for various reasons, such as invalid data or server-side issues. When an error occurs, the response will include a description of the error. The error response will be formatted as follows:

```
{:status :error :data {:text <error-message>}}
```

- <error-message>: A description of the error (as a string).

#### Example Error Response

```
{:status :error :data {:text "Invalid patient identifier format."}}
```

In case of an error, the API will return an appropriate HTTP status code and error message to help diagnose the issue.



### Methods
The following methods are available:

1. `:status`
2. `:list-patients`
3. `:get-patient`
4. `:create-patient`
5. `:delete-patient`
6. `:update-patient`

For each method, detailed descriptions of the parameters, expected request and response formats, and error handling are provided below.

#### :status
*Purpose:* Check the backend status and ensure it's up and running.
*Parameters:* None.
*Response format:* A map containing a :message key with the value "Backend is up and running".

#### :list-patients
*Purpose:* Retrieve a list of all patients.
Parameters: None.
*Response format:* A map containing a :patients key, with the value being a vector of maps representing individual patients.

#### :get-patient
*Purpose:* Retrieve a single patient by their identifier.
*Parameters:* :patient-identifier - UUID of the patient to retrieve.
*Response format:* A map containing a :patient key with the value being a map representing the requested patient.

#### :create-patient
*Purpose:* Create a new patient with the provided data.
*Parameters:* :patient-data - A map containing the patient data (name, age, etc.).
*Response format:* A map containing a :patient-identifier key with the value being the UUID of the newly created patient.

#### :delete-patient
*Purpose:* Delete a patient by their identifier.
*Parameters:* :patient-identifier - UUID of the patient to delete.
*Response format:* A map containing only the :status key with the value :ok.

#### :update-patient
*Purpose:* Update an existing patient's data.
*Parameters:*
- :patient-identifier - UUID of the patient to update.
- :patient-data - A map containing the updated patient data (name, age, etc.).
*Response format:* A map containing a :patients key with the value being a map representing the updated patient data.

## Patient Record Structure

Patient record has the following structure:

- `:patient/identifier`: UUID (optional) -- added automatically
- `:patient/name`: A collection of maps with the following keys:
    - `:name/use`: One of "usual", "official", "temp", "nickname", "anonymous", "old", "maiden"
    - `:name/text`: Non-empty string
    - `:name/family`: Non-empty string
    - `:name/given`: Collection of non-empty strings (the first string is used as the patient's first name)
    - `:name/prefix`: Collection of non-empty strings (optional)
    - `:name/suffix`: Collection of non-empty strings (optional)
    - `:name/period`: A map with the following keys (optional):
        - `:period/start`: A valid date in "yyyy-MM-dd" format (optional)
        - `:period/end`: A valid date in "yyyy-MM-dd" format (optional)
- `:patient/address`: A collection of maps with the following keys:
    - `:address/use`: One of "home", "work", "temp", "old", "billing"
    - `:address/type`: One of "postal", "physical", "both"
    - `:address/text`: Non-empty string
    - `:address/line`: Non-empty string
    - `:address/city`: Non-empty string
    - `:address/country`: Non-empty string
    - `:address/postal-code`: Non-empty string (optional)
    - `:address/state`: Non-empty string (optional)
    - `:address/district`: Non-empty string (optional)
    - `:address/period`: A map with the following keys (optional):
        - `:period/start`: A valid date in "yyyy-MM-dd" format (optional)
        - `:period/end`: A valid date in "yyyy-MM-dd" format (optional)
- `:patient/gender`: One of "male", "female", "other", "unknown"
- `:patient/birth-date`: A valid date in "yyyy-MM-dd" format
- `:patient/insurance-number`: A 16-digit non-empty string

Please note that all keys, unless specified as optional, are required.

## Author

Developed by Sergey Sizov (mailto:harrior@gmail.com).
For questions or suggestions, contact via email or GitHub repository.