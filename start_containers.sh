#!/bin/bash

# Nombre del archivo: start_containers.sh

# Comprobar si docker-compose está instalado
if ! command -v docker-compose &> /dev/null
then
    echo "docker-compose no está instalado. Por favor, instálalo e inténtalo de nuevo."
    exit 1
fi

# Construir los contenedores
echo "Construyendo los contenedores..."
docker-compose build

if [ $? -ne 0 ]; then
    echo "Error durante la construcción de los contenedores."
    exit 1
fi

# Levantar los contenedores en segundo plano
echo "Levantando los contenedores..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "Error al levantar los contenedores."
    exit 1
fi

echo "Contenedores levantados exitosamente."