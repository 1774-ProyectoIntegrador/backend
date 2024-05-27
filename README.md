### RentStudio - RESTful API with Spring Boot

👋 ¡Bienvenido a la API RESTful creada en Java con Spring Boot! Aquí encontrarás los requisitos y las instrucciones para ejecutar el proyecto.

## Requerimientos

- **No requiere instalación manual de dependencias**
- **Docker instalado en tu máquina local**
  - Docker Desktop para [Windows](https://docs.docker.com/desktop/install/windows-install/)
  - Docker Desktop para [MacOS](https://docs.docker.com/desktop/install/mac-install/)
  - Docker Engine para [Linux](https://docs.docker.com/engine/install/)
- **IntelliJ IDEA Community/Pro**
- **Postman o Insomnia para pruebas de Endpoints**
- **Cuenta en AWS**
  - Debes tener permisos para S3 Full Access.
  - Guarda tu ACCESS KEY y PRIVATE KEY para usarlas en la configuración.

## Configuración del Proyecto

1. Renombra el archivo `SAMPLE.env` a `.env` y configura tus claves de acceso a AWS S3.
2. Docker no requiere configuración adicional, pero puedes modificar el puerto según tus necesidades.

## Cómo correr el proyecto

### Linux

Ejecuta el script `start_containers.sh` para iniciar rápidamente los contenedores:
```bash
./start_containers.sh
```

### Windows

Ejecuta el script `start_containers.ps1` en PowerShell para iniciar rápidamente los contenedores:
```powershell
.\start_containers.ps1
```

### MacOS

Abre la terminal en la carpeta raíz del proyecto y ejecuta los siguientes comandos:
```bash
docker-compose down
docker-compose build
docker-compose up -f docker-compose.dev.yml -d
```

## Notas Adicionales

- Asegúrate de tener Docker Desktop corriendo antes de ejecutar los scripts.
- Puedes modificar los scripts para adaptarlos a tus necesidades específicas.