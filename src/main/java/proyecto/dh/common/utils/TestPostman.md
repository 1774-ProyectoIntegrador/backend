# Testeo del Administrador por Defecto en la Aplicación

Este documento describe cómo utilizar Postman para testear el usuario administrador por defecto en la aplicación Spring Boot. Se asume que la aplicación está configurada para crear un usuario administrador por defecto con las siguientes credenciales:

- **Email:** `admin@example.com`
- **Contraseña:** `adminPassword`

## Prerrequisitos

1. **Postman:** Asegúrate de tener Postman instalado. Puedes descargarlo desde [Postman](https://www.postman.com/).
2. **Aplicación en Ejecución:** La aplicación Spring Boot debe estar en ejecución en `http://localhost:8080`.

## Pasos para Testear

### 1. Autenticación del Administrador

#### Solicitud de Autenticación

- **Método:** `POST`
- **URL:** `http://localhost:6060/auth/login`
- **Encabezados:**
    - `Content-Type: application/json`
- **Cuerpo (JSON):**

  ```json
  {
      "email": "admin@admin.com",
      "password": "admin"
  }


#### Respuesta Esperada
Si las credenciales son correctas, deberías recibir una respuesta similar a la siguiente:
- **Cuerpo (JSON):**

  ```json
    {
    "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }


### 2. Acceso a Ruta Protegida del Administrador
####  Solicitud de Acceso
- **Método:** `GET`
- **URL:** `http://localhost:6060/admin/dashboard`
- **Encabezados:**
    - `Authorization: Bearer <jwt>`

###  Instrucciones
    1 Copia el token JWT de la respuesta de autenticación (el valor del campo "jwt").
    2 Crea una nueva solicitud en Postman.
    3 Configura el método HTTP a GET.
    4 Ingresa la URL http://localhost:6060/admin/dashboard.
    5 En la pestaña "Headers", añade el encabezado Authorization con el valor Bearer <jwt>, reemplazando <jwt> con el token recibido.
    6 Haz clic en "Send".

####  Respuesta Esperada
   Si el token es válido y tiene los permisos necesarios, deberías recibir una respuesta con el estado 200 OK y un cuerpo que indique el acceso exitoso a la ruta protegida, por ejemplo:
- **Cuerpo (JSON):**

  ```json
    {
    "message": "Admin Dashboard"
    }
