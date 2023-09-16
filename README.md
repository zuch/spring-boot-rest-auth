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

## Endpoints:

**POST** request to `/register`:

**POST** request to `/logon`:

**GET** request to `/overview/<id>`:

**DELETE**, **PUT**, **PATCH** request to any endpoints:

- response code is 405 because the API does not allow deleting or modifying account data

## Data:

Example of a customer JSON object:

```json
{
    "name": "Keanu",
    "surname": "Reeves",
    "address": "Korte Houtstraat 20, 2511CD, Den Haag",
    "dateOfBirth": "1964-09-02",
    "idDocument": "ID_CARD",
    "username": "theone"
}
```

