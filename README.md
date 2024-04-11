# working-with-testcontainers

Integration test example using Testcontainers, Spring Boot, MySQL, Kafka, Backbase events.

## Prerequisites
* Java 21+
* Maven
* Your favorite IDE
* A docker environment supported by [testcontainers](https://java.testcontainers.org/supported_docker_environment/).

## Test scenario

Let’s consider the following scenario: We have a product that will be stored in MySQL database. We are going to update it on a received event from Kafka.

## Usage

To generate source event from json file: `mvn jsonschema-events:events-generation -f pom.xml`

Run example of the [test file](https://github.com/Backbase/working-with-testcontainers/blob/main/src/test/java/com/backbase/testcontainers/ProductPriceChangedEventHandlerTest.java)
