# Nuclio Kotlin Handler (Example)

This example demonstrates how to implement a nuclio function in Kotlin

## Local running and testing:

1. Building a docker image:
    1. Building the java class:
   ```shell
      ./gradlew clean build
   ```
   The jar file `build/libs/user-handler.jar` should be created
    2. Building the nuclio function
   ```shell
        nuctl build --runtime java -v -p build/libs/user-handler.jar nuclio-kotlin-handler -f function.yaml
   ```
   The docker image `nuclio/processor-nuclio-kotlin-handler:latest` should be created
2. Running kafka (redpanda):

```shell
 docker compose up 
```

3. Creatig a topic:

```bash
docker exec -it kotlin-nuclio-example-redpanda-1 rpk topic create -c cleanup.policy=compact -r 1 -p 1 --brokers=localhost:29092 example-topic output-topic
```

4. Deploying the nuclio kotlin function locally:

```shell
  docker run -ti -p 8080:8080 --network=kotlin-nuclio-example_redpanda_network \ 
  -v $(pwd)/function.yaml:/etc/nuclio/config/processor/processor.yaml \ 
  nuclio/processor-nuclio-kotlin-handler:latest
 
```

5. To test the nuclio function:
  - run a kafka tool, e.g. Kadeck and connect to the local kafka brokers (localhost:9092)
  - send test **JSON** messages into the _example-topic_ kafka topic by means of the kafka tools
  - check the _output-topic_ for new messages


