#!/bin/sh

#gradle build --no-daemon
#java -XX:MaxRAMPercentage=80.0 -XX:+UseZGC -jar build/libs/agent_simulation_api-0.0.1-SNAPSHOT.jar

java -XX:MaxRAMPercentage=80.0 -XX:+UseZGC -jar /app/api.jar
