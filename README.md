# 🏦 CyberWallet API

CyberWallet es una API REST segura y robusta desarrollada con Spring Boot 3, que proporciona autenticación JWT, documentación interactiva con Swagger, monitoreo con Actuator y despliegue completo con Docker y PostgreSQL.

---

## 📦 Tecnologías utilizadas

- Java 17
- Spring Boot 3
- Spring Security + JWT (HS256)
- PostgreSQL
- Swagger (SpringDoc OpenAPI 3)
- Docker & Docker Compose
- Actuator (health check personalizado)
- Maven
- Lombok
- Java Dotenv

---

## 🚀 Instalación y ejecución

### ✅ Requisitos previos

- Tener Docker y Docker Compose instalados (único requisito)

### ▶️ Levantar el entorno completo

```bash
git clone https://github.com/tuusuario/cyberwallet.git
cd cyberwallet/walletapi
docker-compose up -d --build
```

Eso levantará:
- `cyberwallet-db` (PostgreSQL)
- `cyberwallet-api` (Backend Spring Boot)

---

## 🧪 Endpoints disponibles

### 🔐 Autenticación (`/api/v1/auth`)

| Método | Endpoint     | Descripción              |
|--------|--------------|--------------------------|
| POST   | `/register`  | Registrar nuevo usuario  |
| POST   | `/login`     | Obtener JWT válido       |

### 🧠 Healthcheck

- `GET /actuator/health` → Verifica salud del backend, conexión a la base de datos y espacio en disco.

---

## 🧾 Documentación interactiva (Swagger UI)

📄 Accedé a Swagger en:

```
http://localhost:8080/swagger-ui/index.html
```

Incluye:
- Autenticación JWT
- Registro y login
- Exploración completa de endpoints

---

## ⚙️ Variables de entorno

Definidas en `.env` y consumidas por Spring vía `DotenvPostProcessor`:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/cyberwallet
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu_password_postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true

JWT_SECRET=W9xB0rFeF7hyY6QlA7p1rHzm2QbjFnE1aTwdAk2oYxA=
```

---

## 🔍 Monitoreo y HealthCheck personalizado

Implementado en `CustomHealthIndicator.java`:
- Verifica espacio en disco
- Conectividad con base de datos
- Simulación de servicio externo (puede extenderse)

Integrado automáticamente en Actuator:

```yaml
GET /actuator/health
```

Docker detecta automáticamente si el backend está *healthy* para levantar otros contenedores.

---

## 🛡️ Seguridad JWT

- Algoritmo: `HS256`
- Clave: Definida por `.env`, codificada en base64
- Verificación de firma, expiración y autorización vía filtros personalizados (`JwtAuthenticationFilter`)

---

## 👨‍💻 Autor

Desarrollado por **Andres QA**, QA Engineer en transición a QA especializado en Seguridad.

---

## 🐳 Estado del sistema

Para verificar que todo esté funcionando:

```bash
docker ps
```

Deberías ver ambos contenedores activos:

```
cyberwallet-api     healthy
cyberwallet-db      up
```

---

## ✅ Roadmap siguiente

- [ ] Validaciones avanzadas en los endpoints (formato, longitud, duplicados)
- [ ] Control de errores global con `@ControllerAdvice`
- [ ] Tests unitarios con JUnit
- [ ] Collection de Postman automatizada
- [ ] Dashboard de métricas y logs

---

> ¡Este proyecto fue diseñado por Andres Simahan, para ser educativo, seguro y profesional!