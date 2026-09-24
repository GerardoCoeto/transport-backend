# Transport Backend

Sistema backend para la gestión de órdenes de transporte desarrollado con **Java 17**, **Spring Boot** y una arquitectura de microservicios.

## Tecnologías

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Spring Cloud Netflix Eureka
- OpenFeign
- Resilience4j
- Swagger / OpenAPI
- JUnit 5
- Mockito
- Docker
- Docker Compose
- Maven

## Arquitectura

El sistema está compuesto por los siguientes servicios:

| Servicio | Puerto | Descripción |
|---|---:|---|
| Eureka Server | 9000 | Registro y descubrimiento de servicios |
| Order Service | 9001 | Gestión de órdenes de transporte |
| Driver Service | 9002 | Gestión de conductores |
| Assignment Service | 9003 | Asignación de conductores a órdenes |
| Auth Service | 9005 | Registro, login y generación de JWT |

Cada microservicio utiliza su propia base de datos PostgreSQL.

## Estructura del proyecto

```text
transport-backend
├── EurekaServer
├── order-service
├── driver-service
├── assignment-service
├── auth-service
├── docker-compose.yml
└── README.md
```

## Order Service

Permite:

- Crear órdenes.
- Consultar una orden por ID.
- Listar órdenes.
- Filtrar por estado, origen, destino y fecha.
- Cambiar el estado de una orden.

Estados disponibles:

```text
CREATED
IN_TRANSIT
DELIVERED
CANCELLED
```

Transiciones permitidas:

```text
CREATED -> IN_TRANSIT
CREATED -> CANCELLED
IN_TRANSIT -> DELIVERED
IN_TRANSIT -> CANCELLED
```

Una orden en estado `DELIVERED` o `CANCELLED` ya no puede cambiar de estado.

### Endpoints

```http
POST  /orders
GET   /orders
GET   /orders/{id}
PATCH /orders/{id}/status
```

## Driver Service

Permite:

- Registrar conductores.
- Consultar conductor por ID.
- Listar conductores activos.

### Endpoints

```http
POST /drivers
GET  /drivers/{id}
GET  /drivers/active
```

## Assignment Service

Permite asignar un conductor a una orden.

Reglas de negocio:

- La orden debe estar en estado `CREATED`.
- El conductor debe estar activo.
- Una orden no puede tener más de un conductor asignado.
- Se pueden adjuntar archivos PDF.
- Se pueden adjuntar imágenes PNG o JPG/JPEG.

La comunicación con `order-service` y `driver-service` se realiza mediante **OpenFeign**.

Se utiliza **Resilience4j** para manejar fallos de comunicación y ejecutar fallbacks cuando un servicio no está disponible.

### Endpoints

```http
POST /assignments
POST /assignments/{id}/pdf
POST /assignments/{id}/image
```

## Auth Service

Se encarga del registro de usuarios y autenticación mediante JWT.

### Registrar usuario

```http
POST /auth/register
```

Ejemplo:

```json
{
  "username": "admin",
  "password": "123456"
}
```

### Login

```http
POST /auth/login
```

Ejemplo:

```json
{
  "username": "admin",
  "password": "123456"
}
```

Respuesta:

```json
{
  "token": "eyJ..."
}
```

## Seguridad

Los endpoints protegidos utilizan **Spring Security + JWT**.

Las peticiones autenticadas deben incluir:

```http
Authorization: Bearer TU_TOKEN
```

Ejemplo:

```http
GET http://localhost:9001/orders
Authorization: Bearer eyJ...
```

## Eureka

La consola de Eureka está disponible en:

```text
http://localhost:9000
```

Servicios registrados:

```text
ORDER-SERVICE
DRIVER-SERVICE
ASSIGNMENT-SERVICE
AUTH-SERVICE
```

## Bases de datos

Cada servicio utiliza una base PostgreSQL independiente:

| Base de datos | Puerto externo |
|---|---:|
| orders_db | 5433 |
| drivers_db | 5434 |
| assignments_db | 5435 |
| auth_db | 5436 |

Dentro de Docker, PostgreSQL utiliza el puerto `5432`.

## Docker

Toda la arquitectura puede levantarse con Docker Compose.

### Construir y levantar

Desde la carpeta raíz:

```bash
docker compose up -d --build
```

### Ver contenedores

```bash
docker compose ps
```

### Ver logs

```bash
docker compose logs
```

Ejemplo para un servicio:

```bash
docker compose logs order-service
```

### Detener el sistema

```bash
docker compose down
```

## Flujo general

```text
Cliente / Postman
       |
       | JWT
       v
+--------------------+
|    Auth Service    |
+--------------------+

       |
       +-----------------------------+
       |                             |
       v                             v
+---------------+             +----------------+
| Order Service |             | Driver Service |
+---------------+             +----------------+
       ^                             ^
       |                             |
       +-------------+---------------+
                     |
                     v
             +--------------------+
             | Assignment Service |
             +--------------------+
```

`Assignment Service` consulta órdenes y conductores mediante OpenFeign.

## Resilience4j

Resilience4j se utiliza como Circuit Breaker para manejar fallos entre microservicios.

Ejemplo:

```text
assignment-service
        |
        v
driver-service no disponible
        |
        v
Circuit Breaker
        |
        v
Fallback
```

Esto permite devolver respuestas controladas cuando un servicio externo no está disponible.

## Pruebas unitarias

Se utilizaron:

- JUnit 5
- Mockito

Se probaron escenarios como:

- Creación de órdenes.
- Búsqueda de órdenes inexistentes.
- Cambios de estado válidos.
- Cambios de estado inválidos.
- Asignación correcta de conductor.
- Rechazo de conductor inactivo.
- Rechazo de órdenes que no estén en `CREATED`.
- Prevención de asignaciones duplicadas.

Para ejecutar pruebas:

```bash
mvn test
```

## Swagger / OpenAPI

Cada microservicio expone documentación Swagger.

```text
http://localhost:9001/swagger-ui/index.html
http://localhost:9002/swagger-ui/index.html
http://localhost:9003/swagger-ui/index.html
http://localhost:9005/swagger-ui/index.html
```

## Ejecución rápida

1. Iniciar Docker Desktop.
2. Clonar el repositorio.
3. Entrar a la carpeta principal del proyecto.
4. Ejecutar:

```bash
docker compose up -d --build
```

5. Abrir Eureka:

```text
http://localhost:9000
```

6. Registrar un usuario.
7. Iniciar sesión.
8. Copiar el JWT.
9. Consumir los endpoints protegidos enviando:

```http
Authorization: Bearer TU_TOKEN
```

## Autor

**Luis Gerardo Coeto Rivera**

Ingeniero en Tecnologías de la Información
