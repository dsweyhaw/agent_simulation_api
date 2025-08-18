#!/bin/bash

# Build docker and start database container
docker compose build db redis
docker compose up db redis -d

# Add host.docker.internal to /etc/hosts to allow the container to access the host
if ! grep -q "0.0.0.0 host.docker.internal" /etc/hosts; then
  sudo echo "0.0.0.0 host.docker.internal" | sudo tee -a /etc/hosts > /dev/null
fi

# Build api
./gradlew clean build
docker compose build

# Start the project
docker compose up
