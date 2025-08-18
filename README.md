# Agent Simulation API
## Setup environment
### Install Java 21 or later
Since installing Gradle takes a long time in the Docker container, we need to build the project first before running docker-compose.
#### Linux distro (Ubuntu/Debian)
```
sudo su

apt update
apt install wget
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb
sudo apt install ./jdk-21_linux-x64_bin.deb
rm -rf ./jdk-21_linux-x64_bin.deb
java --version
exit
```

#### MacOS
```
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
brew install openjdk@21
```

## After clone
```
access project

cd src/main/resources
cp application.yml.example application.yml
cp application-dev.yml.example application-dev.yml
cp application-local.yml.example application-local.yml
```
Config base on your environment

### Active environment
Go to src/main/resources/application.yml and change the active profile to local, dev or ...
```
Example change environment to local

spring:
  profiles:
    active: local
```

## Run project
```
sh scripts/start-project.sh
```

## Run project manually
```
docker compose up db -d
./gradlew build
java -jar build/libs/agent_simulation_api-0.0.1-SNAPSHOT.jar
```

## Log
See at storage/logs/spring.log

## Insert master data
```
docker exec -i ags_local_db mysql -uagsuser -p123 ags < ./src/main/resources/db/sql/master_data_local.sql
docker exec -i ags_local_db mysql -uagsuser -p123 ags < ./src/main/resources/db/sql/import-pig-farm-multi-simulation-project.sql
docker exec -i ags_local_db mysql -uagsuser -p123 ags < ./src/main/resources/db/sql/import-klinh-pig-farm-emission-project.sql
docker exec -i ags_local_db mysql -uagsuser -p123 ags < ./src/main/resources/db/sql/import-dhung-pig-farm-env-project.sql

docker exec -i ags_dev_db mysql -uagsuser -pmysql123456 ags < ./src/main/resources/db/sql/master_data_dev.sql
docker exec -i ags_dev_db mysql -uagsuser -pmysql123456 ags < ./src/main/resources/db/sql/import-pig-farm-multi-simulation-project.sql
docker exec -i ags_dev_db mysql -uagsuser -pmysql123456 ags < ./src/main/resources/db/sql/import-klinh-pig-farm-emission-project.sql
docker exec -i ags_dev_db mysql -uagsuser -pmysql123456 ags < ./src/main/resources/db/sql/import-dhung-pig-farm-env-project.sql

rm -rf /Users/trungdt/Workspaces/uet/agent_simulation/gama/outputs/**
rm -rf /Users/trungdt/Workspaces/uet/agent_simulation/gama/xmls/**
```
