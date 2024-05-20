#!/bin/sh

# Esperar a que los contenedores estén levantados (puedes ajustar el tiempo de espera según tus necesidades)
sleep 30

# Eliminar imágenes colgantes (dangling)
docker image prune -f

echo "Limpieza completada."