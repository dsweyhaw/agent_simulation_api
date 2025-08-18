#!/bin/sh
gradle build
java -jar build/libs/agent_simulation_api-0.0.1-SNAPSHOT.jar
