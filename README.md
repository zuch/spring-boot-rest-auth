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

### build

```bash
mvn clean install
```

### test

```bash
mvn clean test
```

## PostMan Collection

Import the PostMan Collection with `Customer_Onboarding_API.postman_collection.json` file found under root location

## Open API

The Open API UI for the endpoints below can be seen with http://localhost:8080/swagger-ui/index.html once the application is running

## Endpoints:

### **POST** `/register`:

`POST`: http://localhost:8080/register

#### Request Headers:

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

#### Response Status Codes:

`200`: If Customer Registered successfully

`400`: If validation rule triggered

`429`: If more than 2 requests per second sent

#### Example of a Registration response:

```json
{
  "username": "theone",
  "password": "AY3QbioL2k"
}
```

### **POST** `/logon`:

`POST`: http://localhost:8080/logon

#### Request Headers:

`Content-Type`: `application/json`

`Accept`: `application/json`

#### Example of a Logon request:

```json
{
  "username": "theone",
  "password": "AY3QbioL2k"
}
```

#### Response Status Codes:

`200`: If Username & Password authorization successfully

`401`: If Username & Password authorization denied

`429`: If more than 2 requests per second sent

#### Example of a Logon response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU"
}
```

### **GET** `/overview`:

`GET`: http://localhost:8080/overview

#### Request Headers:

`Accept`: `application/json`

`Authorization` : `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU`

#### Response Status Codes:

`200`: If JWT Token authorization successfully

`401`: If JWT Token authorization denied

`429`: If more than 2 requests per second sent

#### Example of a Overview response:

```json
{
  "iban": "NL62VLET1423701633",
  "accountBalance": 286663.71,
  "accountType": "CURRENT",
  "currency": "EUR",
  "openingDate": "2023-09-20T18:17:46.065199"
}
```

