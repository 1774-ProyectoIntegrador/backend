### RentStudio - RESTful API with Spring Boot

👋 Bievenido a la API RESTful creada en Java junto a Spring Boot, aquí encontrarás requisitos y como ejecutar el proyecto.

## Requerimientos

- Java SDK 17 minimo
- Docker Instalado en su maquina local
    - Docker Desktop para [Windows](https://docs.docker.com/desktop/install/windows-install/)
    - Docker Desktop para [MacOS](https://docs.docker.com/desktop/install/mac-install/)
    - Docker Engine para [Linux](https://docs.docker.com/engine/install/)
- IntelliJ IDEA Community/Pro
- Postman o Insomnia para pruebas de Endpoints
- **Cuenta en AWS**
    - Debe poseer permisos para S3 Full Access.
    - Debe guardar su ACCESS KEY y PRIVATE KEY para ser utilizada.

## Configuración del Proyecto

1. Renombrar el archivo `SAMPLE.env` a `.env` y configurar sus claves de acceso a AWS S3.
2. Docker no requiere ninguna configuración, puede modificar el puerto a su necesidad.

## Como correr el proyecto

- Si está en **Linux** puede ejecutar el script "start_containers.sh" para ejecutar rapidamente.
- Si está en **Windows** o **MacOS** ejecute los siguientes comandos abriendo la terminar en la carpeta raíz del proyecto:
```
docker-compose down
docker-compose build
docker-compose up -d
```

