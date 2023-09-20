# Spring Boot REST API using Authentication

## Environment:

Uses:

- Java version: 17
- Maven version: 3.*
- Spring Boot version: 3.1.3
- Database: PostgreSQL 16

Required

- _docker-compose_
  3.8 [compatibility-matrix](https://docs.docker.com/compose/compose-file/compose-versioning/#compatibility-matrix)
- Only _docker-compose_ required to run environment!

## Commands

### run

```bash
docker-compose up
```

_docker-compose_ will bundle the spring boot app into docker image.

_docker-compose_ will then start a docker container of the spring boot app together with a postgres container

`NOTE!` The first time you execute `docker-compose up`, the spring boot app may be trying to reach connection to
postgres before
available.
If so please rerun `docker-compose up`

### build

```bash
mvn clean install
```

### test

```bash
mvn clean test
```

## Open API

The Open API UI for the endpoints below can be found with this [URL](http://localhost:8080/swagger-ui/index.html) once
the
application is running

## Endpoints:

### **POST** `/register`:

`POST`: `http://localhost:8080/register`

`Content-Type`: `application/json`

`Accept`: `application/json`

#### Example of a Registration request:

```json
{
  "name": "Keanu",
  "surname": "Reeves",
  "address": {
    "street": "Korte Houtstraat",
    "houseNumber": "20",
    "postCode": "2511CD",
    "city": "Den Haag"
  },
  "dateOfBirth": "1964-09-02",
  "idDocument": {
    "type": "ID_CARD",
    "idNumber": 12345678,
    "countryCode": "NL",
    "issueDate": "2015-02-01",
    "expiryDate": "2025-01-31"
  },
  "username": "theone"
}
```

#### Example of a Registration response:

`200`: If Customer Registered successfully

`400`: If validation rule triggered

```json
{
  "username": "theone",
  "password": "AY3QbioL2k"
}
```

### **POST** `/logon`:

`POST`: `http://localhost:8080/logon`

`Content-Type`: `application/json`

`Accept`: `application/json`

#### Example of a Logon request:

```json
{
  "username": "theone",
  "password": "AY3QbioL2k"
}
```

#### Example of a Logon response:

`200`: If Username & Password authorization successfully

`401`: If Username & Password authorization denied

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU"
}
```

### **GET** `/overview`:

`GET`: `http://localhost:8080/overview`

`Accept`: `application/json`

`Authorization` : `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU`

#### Example of a Overview response:

`200`: If JWT Token authorization successfully

`401`: If JWT Token authorization denied

```json
{
  "iban": "NL62VLET1423701633",
  "accountBalance": 286663.71,
  "accountType": "CURRENT",
  "currency": "EUR",
  "openingDate": "2023-09-20T18:17:46.065199"
}
```

**DELETE**, **PUT**, **PATCH** request to any endpoints:

- response code is 405 because the API does not allow deleting or modifying account data

