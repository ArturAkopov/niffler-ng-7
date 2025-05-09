#!/bin/bash
source ./docker.properties
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export FRONT_VERSION="2.1.0"
export COMPOSE_PROFILES=test
export ARCH=$(uname -m)

echo '### Java version ###'
java --version

if [[ "$1" = "gql" ]]; then
  export FRONT="niffler-ng-gql-client"
else
  export FRONT="niffler-ng-client"
fi

docker compose down

docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ "$1" = "firefox" ]; then
  export BROWSER="firefox"
  docker pull selenoid/vnc_firefox:125.0
fi

for image in "postgres:15.1" "confluentinc/cp-zookeeper:7.3.2" "confluentinc/cp-kafka:7.3.2" "${PREFIX}/niffler-auth-docker:latest" "${PREFIX}/niffler-currency-docker:latest" "${PREFIX}/niffler-gateway-docker:latest" "${PREFIX}/niffler-spend-docker:latest" "${PREFIX}/niffler-userdata-docker:latest" "${PREFIX}/niffler-ng-client-docker:latest" "aerokube/selenoid:1.11.3" "aerokube/selenoid-ui:1.10.11" "${PREFIX}/niffler-e-2-e-tests:latest" "frankescobar/allure-docker-service:2.27.0" "frankescobar/allure-docker-service-ui:7.0.3"; do

  if [[ "$(docker images -q "$image" 2> /dev/null)" == "" ]]; then
    bash ./gradlew clean
    bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test
    echo "### Building images ###"
    break 2
  fi
done

docker pull selenoid/vnc_chrome:127.0
docker compose up -d
docker ps -a
