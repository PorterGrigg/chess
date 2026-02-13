# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Chess Web API Diagram by Porter Grigg](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=C4S2BsFMAIGEAtIGcnQOqQEbQIIAUBJaAWjwHsAnYSC6AcQpAHMmAoVgQwGNhK5wQkAHbBWABw5UQXEBJHQAyjQBuNcZNAy5waAAkOQgCZQK6qVoM6lFZdMhnNsy9AAiHYBxxcuyJK0PuHJgcSDCGmOzCwBQAnkgSMkJM0AAMAHQAnKxMFGQArmIAxAAsAMwAHABMGVzQhQJM8MCY4HkwAEqQTCBI0e4gZEKssAJRJAB8iio0AFzQANp4APIKACoAutAA9HmhFAA6QgDeAES7NEIcALaQJzMn0CcANI8SKADulIZ3D8+PkFcOCBwD9HgBfVjWVS0YiTfRGExzU7nCiXG6gv4nN5IT4Ub73R4vE4AoEggknCHw4w0CZTGx2OYULo9agUAAUnW6vRonQAjm1egBKSEqOy0tweLw+FBzJiQYAAVT2bJRaMgwolnm8vlp4RmADEQEZoEqaJroJgYtBVdd7JgmRwANYmvbQd5geDWvZq6AccAOwxWyAADxZfk1Up1xFhdNsPjmpoompF9J8JBjUNmuH9kA4gdWTuEAFFgz4xKBBinoenJiNBCI5sUUqVDqcbigOHK7o8ixRchQ5jabr6c3mrR5HcIKaxhIZWBHtSga7GGdAhHlwOAq2KYwvpUgZlwHdREyq9pqNYFI0uY3qcIZDC6zYFt2nd1fFwej7nqDg8sB4DZDh-3gC95w-fddUwGZ70fP8AOTKEdxjKlEWgTkWR5ZAN1EVCaWjSZMwHaBkW9W0HjmX4iWAgDVjISchAox5wSrfCYzrKI5kqFIUlbM4yPRaBKMJR4aPgOiGKY34IVndgcnyIpKAMOU6n1WAixcWAcGgAAZMhuiGDj5AzaZiMWFYNm2UIUAGIQ+KHW57kxbFcXxE5pyI2k8IHUiLnIpyiRcr4fmnbzaSQ+NoHAfSjTZaKDM6JAcOFCKYHfSVP1leVTwcy8Msg29oMNY1E3NS0vT8m5wPyqMTNTSAE3PF97VzZ0ACFHyZflkGAF4-QDK14rlR8jWqrUCrquMGrXDct1S5ciLmNq8z5AVgBLMsKyGTyCP4etgEbHi+PbJBO0cns+0oOZgk6yBut6DzRTfSasxwUd80LIQNsgctbNYmF2NGBtoCbFtjhOE6zu7E5e37OZ+tzQNoAnKcZKMV80smPdfEHMQAhPPYFA8YBdiAkCJOEPLxqjSY9QVPH3BgUrAgtK0xIpoYWqdOB8nAR8hDIHR4tG7Gb0m1d103DGFtMuZvt+ysdsB-a5gAViO8HIa7SjYauljZNF1BdtSxrnw8aXdu8uZhaERKcNYMLjdlkj+MqxyqNeEIcWC8kiRJYEMQhJXayBg7oG43jwYcjFAq91yY-+QEA-JNG52yXICkKHJIGEOoGiabPc70ph8lEIydBesyXCLHSi1WItNi2aykFsw4xMoEAAC9+kGOYAB52fo4Rxn+ryDGpAd28Ybutv7weGJHx3CKe6b4tLuL9NL1aepSlfxQgnHoDlYB4MQERpEZsnaKHoQqevI3aaKo04JAsq2fJm-WC551E2gHo10Fr6ECHdO6QDnIbGW9U5iSzmnvJ2NgswKkuMA6eYD5ZbVHrtcuh0ACMx1fBQx1pdYieRkEARAWA6cBsD5i2XlA3Ar8XzzXStTGU1oGYExoETdwpN56UzGvfKCMx6b4yZk1DwrMgHXwYl-B0zpYC835oAte-4BGfkgVNaBs0LaVzlqWH6GDg57U4tAdWkc2wEO1hdOG+t0YQPgZop8SYmFwJQuPNCKjgB23ALhdxbE6HQiREHUyy5sHhw1kcVOckM5FCZI+QoYgjTOh0iyegto-Dl3Cs7eYdA66NyYLaNuKCu49yEHPD+C9MFwj8ZPYpM9bLlOkcPB2NSsn0IEL0OKLJt5CgtljGhB4j7ylPlEC+1Ar7iRvnfdRhUDTPwYQhFm5U+GczkU4v+qABY6Cnl3MBaiJoBIlto5huiTRkPgBQww6C-pGLCU2PBmtLHnRhsQwc5zLlULsQMjRq5T6ITgf0mqbDj5vXAHQdJbJpkFUfnM40fpwBpJuMmb+PMNxKKFiyfZtVDmRRgTogJWZrmKxCVg0OasIkQyedDXWxFPngO+Q41c4KkUuPqsuK2UVunYR8S0hE-jYxZlOAU9s3Z5hCttAQFw3YcGVFKMUIk7x4BgEgImNUIUiQtG4I6VV-l3JgmgOsYJCCAYhxVuE8xJxhXIFFeKm4krpWyvlY8RVyqdXogCo8TVXBtUCXdhSA1UTokKUKIYAA7BkFIkAUh1CLKUWA6kABscBjwwGZTAYYoc2mBIWMsNY+TCnvOnqUxpkyF58StQAOV1Y9Y1Y8+W1PIUW2e0AB4VOHuW20Vb3V6t5RPLNq5vyX0HdQHpwBd5spYffLKJ8QKjK4JfFZUKabQD1MVF+iyJHLLbas1q6z-5bKkRc1B9KgVG3Fri45riCXESQTs0BVz9EK22iS5WJj7n4I7FYl5NjSF3soVE+x57pp-NZVNfep7p1pq7ZANklbbRLpvDCtdiLIBvyPp220sjd3LVuvdXq0AuCKIAToYd9hAM4ummm-5E7Tk4dHUS59tbSVmqbBarWzyaXXTzNALqa0a1gYcYSx9hiX2mpMWYj9p0v2cdsSe1hZ6KNaKlswwF8mZh5kMGm2DtpNR9UYR4BDD8V3QVguh4cRoUPUYE24+tcxSPeN8fWjRgqjXVmYyYiOrZA3p2DTESAm4yDvDzswJofmAtBYAFJkAs2muoXrHQZv2lmrMiwFSWS2FaopjaSnNtbU0oQ4w+JiHABwPzFAFHRQoJiNAugCD1y2G1HSOBYAAGlMRWvtXMGVcrXP4WqbZw9IDi0tpWYV8GxXSs0Aq5QartX6uNea21okHWpVdcdZSVpQG5gACtotCDZFFo0o7x0CdU1OoZM6AJzoXduwzQjkMgc3e-fLWHua-33YAv9cnBGMovcpq9Aqb2Ft2Q+zaNzRPGOBu+x5n6OOvOtMD+9dKsW0JXJFR7HA+muAGZB202m7UuDu7M5DVGllWhWyjhTaPKM6eams2L-9f3KFJEEKALwbo8bumtF4hG0XEegLtkWDKtszX+zR69S0Vpc56gxzBr6ocUvY9S+HHPeM9X48hCX2YBoFgYrL25ZKQbNkk4Q6xesEZjmRp9DXz0tf64h2EiTMOpNw5scjlT2OIMFFEXgErZX8eocCHpjdHAXgTbK9NigROYUiMZmZmAMXafmxOf1iecxDu225Y5vtgniKRLl2J4GnnjiBvkpnHIpXguNGABXq0Ixcy0A4GIYrYzbLQES2MU58xq613ro3cIVS9A1JmKcLgUBJChRqbCE2BHx-shO3YSdmUV3+flJAUFkLKewj1K4Vf1BswIuZh4F4pPj++mNH84mmOUUKL5wesfDfKfOeIvbpj8uw7zDMesE30n4fI8NgRDPniqlJbMPqLluI7KcqcPxvymEsXvnpEOjEAA)

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
