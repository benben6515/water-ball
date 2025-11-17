#!/bin/bash
cd "$(dirname "$0")"

# Load environment variables from parent .env file
export $(cat ../.env | grep -v '^#' | grep -v '^$' | xargs)

# Start Spring Boot
./mvnw spring-boot:run
