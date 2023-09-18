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

Example of a Customer Registration object:

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

### **POST** `/logon`:

`POST`: `http://localhost:8080/logon`

`Content-Type`: `application/json`

`Accept`: `application/json`

Example of a Customer Logon object:

```json
{
  "username": "theone",
  "password": "w2kfHZriif"
}
```

### **GET** request to `/overview`:

`POST`: `http://localhost:8080/logon`

`Content-Type`: `application/json`

`Accept`: `application/json`

**DELETE**, **PUT**, **PATCH** request to any endpoints:

- response code is 405 because the API does not allow deleting or modifying account data

