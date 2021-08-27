# Analysis Hub

## 1. Setup

1. If another Neo4j instance instead of the provided one is used, the `neo4j.host` setting hast to be changed in the file **/src/main/resources/application.properties**.

1. Spin up the Analysis Hub and its Neo4j Database. You can specify the docker network the services should connect to in `docker-compose.yml`.
    - Use `docker-compose build` to build the containers.
    - Use `docker-compose up` to start the containers.
    - Use `docker-compose up --build` to rebuild the containers and start them up.


## 2. Usage

You can view the application at http://localhost:8545 in your browser.
