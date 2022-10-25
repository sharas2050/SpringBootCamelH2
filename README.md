# SpringBootCamelH2
API which will enable "Tasks Management System" to save and query tasks status.

Manipulation of the data (save, update etc.) implemented using Apache Camel.

Data saved in in-memory database hence data is lost after application restart. Configuration can be changed to embed data to file. 

Tests available for service and route controller.

Postman collection included.
## Pre-requisites ##
* JDK11+
## Running the application locally

First build with:

    $mvn clean install

Swagger UI:

    http://localhost:8080/swagger/index.html

Requests for all routes are included in the postman collection under 

    /doc/curl
