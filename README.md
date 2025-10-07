# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+uB5afLUKw-Pofw7F80KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU7KVyN4+XeS7CmKEpujKcplu8SqYCqwYagAchAG6wDqeqxUaVo2sFvIjkm5SebUPZ9tuKBFaB9SpW6UYxnGhRaUV8DIKmMAAKx4QROaqHm8zQUWJb1IFMBoBAzAAGa+JwDb0QlqoauKVAmkg65qTs+ULqoVXOl2UVbp5u0SXUzI9nq0BIAAXigHD1SgsYKeh8LJm12EwOmACMPX8v1BZjEN0AjdMl6XTdux0U2w78sdNkugd8gVRU0P0oecgoM+8TnpeWjyHOIUw+UD5rgGl6vntRU6aWznihkqgAZgVOw7phH6fMJGod8FFUfWHO0c9mFvWAOHdaMrNxcRpFc5ePPIXzaHzUxfj+F4KDoDEcSJKr6vOb4WCiYK1WNNIEb8RG7QRt0PRyaoCnDNziHoM9FRU7U4wO9RQIWbClAVCVdlCXrTmB6erlqO5SPlCjo4wIyYCY9j8GO2g+MFYTy6TeKT5k9osryh7TuJeqMDLat64F4U0ew0iZX9kdfvVM8Z2g1A123fdj3xs1r0lMLH1ON9Yu9X9g3FkDCogxdrfg3NUO3jDDediuOeI55grR7UHAoNwx6XonlHJ6n20VMuW874YmPRfnMvJ0jLuWc8uuh-+CCAQ-UDM3UIyaY3H8VKJtRcL4VGJDBinhvDKxROufw2BxQan4miGAABxJUGgDbWUkkg82Vt7BKntjfJCztyiuwrozd+i84ZdmQDkFBOZ96yw8hTKO896RxwThXI++4T7CkzhKS+ucYqkKLktNAK1kDlwIU7KuFCa69jrhTChTdzrxDBu3aMD1GoCxagA-ug9sy-XzKPYaE8KKqIho2Bi0jip7VqBRcmnZkYsNHNQsAtC1AYk4YVImwoXGVgQLHVBW0uEyJdLguY9jKGU3frUBBOQ6YMyZoor+ywwlqALA0cYqSACS0gCyfXCMEQIIJNjxF1CgN0nI9jfGSKANUFTIKLG+Kk5KSpGkXBgJ0H+J0ioAKAVmFJqD0mZKVDkvJBSinLBKWU+pBkxjVIQLUmZA05kgmaa0uZ7TOmmHmuA5i-gOAAHY3BOBQE4GIEZghwC4gANngBOQwbiYBFCFobX+9QpIdBwXgyeB8kJZjWXMLpNR74+zqBXNYwy5gtMlpzb2X4QldjRuiNxGI4D3LcWHEkkcN5sL3hwoJXiM5Z1Ji+AR18k6EOESXURZdjSSMrk46uLpa532sSdWozcp5tzuuozuTVf6C17mmAeP1cyGMLGPUsPgflmNnpYxlCLl6ktXkwjeSKUAotSdeKu3jbEk1SUYQ6CjiHRLRUeDVSp4mvzIaCz+bsxjZNybUfJhSgW+3-kLQBotIUoFGc68ZoDGIQICJYbe9lNgayQAkMAoa+wQAjQAKQgOKZBFZ-A1JAGqZ5vdXnssaE0ZkMkeipPwRS9AWZsALNDVAOAEB7JQDWI6t1H8TWgtsfSlYIxK3AGrbW+tjaRnSDhXCRVAArZNaAUVJvFBilAhJw6MIccwgmrCmTsPpZ49OPDiX8PkHnOlZbChUtLuIg9vypEKrZZQ0qcjWXVU5So6eaiGpPW7pUHRX1RV9XFQDSVwNTFPvMfNKxJVd3AGxU42ocdNWDs3eobhtReHrgNTFR1C0kowBsBNY0ZoazhgZSunaiqtVGocUk2oZBsATTDEhDumjQI9M9emTMaAYAjGWMPH9gNSwmlwzAWsvNA0gZsSRlVS6N5+FxhauYGJRPADgztXVkn0YBLmDAFarddDcA0PXVtX5ajTsnZal+b9bXke-s7D1Qq+kgIsUGvZXge2RujY5+UiBgywGANgSthA8gFCeeg30bzjam3NpbXoxgiGuxtfCq9SIQDcDwB4iDhGxyJagMlnVy53N4D8TACAk1+PxEMBAHQo6UDgAFJNbwMwiuGEi7p12OWoBWtM-C8zzbGPWe9YGoAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
