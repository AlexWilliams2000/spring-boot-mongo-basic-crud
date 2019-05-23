# Spring Boot Basic CRUD with Mongo

A basic Spring Boot web app exposing REST endpoints and using Mongo for persistence. Includes unit tests and a Postman collection and environment for API testing.

## Functionality

The service provides various endpoints:
  - Create one person
  - Create many persons
  - Read a person by id
  - Read persons by name
  - Read all persons in the database
  - Update a person
  - Delete a person

Currently a person comprises `id` and `fullName` fields only but the model could easily be extended.

## Clone the Repo

```git clone git@github.com:AlexWilliams2000/spring-boot-mongo-basic-crud.git```

## Build the Service

  ```./gradlew clean build```

## Dependencies

 - A running instance of Mongo with no security restrictions.

## Run the service locally

  ```./gradlew bootRun```

## Port

The API is exposed on port `9000`.

## Health Check and Info

To ensure the service is up and running, send the following requests:

### Bash

  ```curl http://localhost:9000/actuator/health```

### Powershell

  ```Invoke-WebRequest -Uri http://localhost:9000/actuator/health```

The response should be:

```
{
    "status": "UP"
}
```

## Api Testing

The repo includes a Postman collection containing a set of test requests in src/test/resources/postman. To run these:

 - Import the collection into Postman
 - Import the environment and select it
 - In the Collections sidebar, click the arrow  to the right of the `Spring Boot App Api Testing` collection to expand the set of requests and click `Run`, then click `Run Spring Boot App Api Testing` in the collection runner, or:
 - Launch the Collection Runner with the `Runner` button, select the collection and click `Run Spring Boot App Api Testing`