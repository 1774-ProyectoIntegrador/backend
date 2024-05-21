#!/bin/bash

# File name: start_containers.sh

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null
then
    echo "[JACKMOON-DEV] docker-compose is not installed. Please install it and try again."
    exit 1
fi

# Stop and remove existing containers
echo "[JACKMOON-DEV] Stopping and removing existing containers..."
docker-compose down

if [ $? -ne 0 ]; then
    echo "[JACKMOON-DEV] Error while stopping and removing containers."
    exit 1
fi

# Build the containers
echo "[JACKMOON-DEV] Building the containers..."
docker-compose build

if [ $? -ne 0 ]; then
    echo "[JACKMOON-DEV] Error during container build."
    exit 1
fi

# Start the containers in the background
echo "[JACKMOON-DEV] Starting the containers..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "[JACKMOON-DEV] Error while starting the containers."
    exit 1
fi

echo "[JACKMOON-DEV] Containers started successfully."