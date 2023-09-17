# REST using Spring Security JWT authentication

## Environment:

- Java version: 17
- Maven version: 3.*
- Spring Boot version: 3.1.3
- Database: PostgreSQL 16
- _docker-compose_ 3.8 to run environment [compatibility-matrix](https://docs.docker.com/compose/compose-file/compose-versioning/#compatibility-matrix)

## Commands

### run
```bash
./run.sh
```

`run.sh` is a shell script that will maven bundle spring boot app then will execute _docker-compose_ to bundle spring boot app into docker image.

_docker-compose_ will then start a docker container of the spring boot app together with a postgres container

`NOTE!` The first time you execute `run.sh`, the spring boot app may be trying to reach connection to postgres before available. 
If so please rerun `run.sh` 

#### terminals to use per operating system:

* `windows` 
  * git bash 
  * wsl
* `macOS`
    * terminal
* `linux`
  * bash

### build
```bash
mvn clean install
```

### test

```bash
mvn clean test
```

## Swagger UI

The Swagger UI for the endpoints below can be found with this [URL](http://localhost:8080/swagger-ui/) once the application is running

## Endpoints:

### **POST** `/register`:

`POST`: `http://localhost:8080/register`

`Content-Type`: `application/json`

Example of a Customer Registration object:

```json
{
  "name": "Keanu",
  "surName": "Reeves",
  "address": {
    "street": "Korte Houtstraat",
    "houseNumber": "20",
    "postCode": "2511CD",
    "city": "Den Haag"
  },
  "dateOfBirth": "1964-09-02",
  "idDocument": {
    "type": "ID_CARD",
    "countryCode": "NL",
    "issueDate": "2015-02-01",
    "expiryDate": "2025-01-31"
  },
  "username": "theone"
}
```


**POST** request to `/logon`:

**GET** request to `/overview`:

**DELETE**, **PUT**, **PATCH** request to any endpoints:

- response code is 405 because the API does not allow deleting or modifying account data

