# 🧠 CyberWallet API

CyberWallet es una billetera virtual moderna y robusta, desarrollada con tecnologías de nivel empresarial. Este backend está construido en Java con Spring Boot, JWT para autenticación, PostgreSQL, y un entorno completamente dockerizado, con tests automatizados y documentación Swagger.

---

## 🧩 Tech Stack

- ⚙️ Spring Boot 3.x
- 🔐 Spring Security + JWT (HS256 con clave hex)
- 🧪 JUnit 5 + Mockito
- 🧪 Tests de integración con MockMvc
- 🐳 Docker + Docker Compose
- 🐘 PostgreSQL
- 🧾 Swagger UI (OpenAPI 3)
- 📄 Logback + logging avanzado
- 🌐 RESTful API
- ☂️ Global Exception Handler

---

## 🚀 Quickstart (Docker)

1. Cloná el proyecto:

```bash
git clone https://github.com/TuUsuario/CyberWallet.git
cd CyberWallet
```

2. Levantá todo el entorno con Docker:

```bash
docker-compose up -d --build
```

3. Accedé a Swagger UI:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔐 Autenticación

La seguridad se maneja con JWT. Se expone un endpoint de login (`/api/v1/auth/login`) que devuelve un token para ser usado en los headers (`Authorization: Bearer <token>`).

Endpoints protegidos: `/api/v1/user/**`

---

## 📘 Endpoints

| Método | Endpoint                  | Seguridad | Descripción                  |
|--------|---------------------------|-----------|------------------------------|
| POST   | `/api/v1/auth/register`   | ❌        | Registro de nuevo usuario    |
| POST   | `/api/v1/auth/login`      | ❌        | Login y generación de token  |
| GET    | `/api/v1/user/me`         | ✅ JWT    | Perfil del usuario autenticado |

Consulta Swagger para más detalles.

---

## 🧪 Testing

Tests incluidos:

- ✅ Unitarios (JwtService, excepciones, DTOs)
- ✅ Integración real con DB embebida (H2)
- ✅ Manejo robusto de errores (401, 403, excepciones custom)

Ejecutar tests:

```bash
mvn clean test
```

---

## 📦 Variables de entorno

`.env` (automáticamente cargado por `DotenvPostProcessor`):

```env
JWT_SECRET=a3f1c6e9b2478f5d92c49f3ee71349afcbe916d3640ffb3a94a6012ccbbd12b3
```

---

## 🛠️ Healthcheck

Se expone un `CustomHealthIndicator` y endpoints `/actuator/health`. Puede extenderse para verificar DB, disco, servicios externos.

---

## 📂 Estructura del Proyecto

```
walletapi/
├── config/               # Configuraciones de seguridad y Jackson
├── controller/           # REST Controllers
├── dto/                  # DTOs de autenticación y otros
├── entity/               # Entidades JPA
├── exception/            # Manejo global de errores
├── repository/           # Interfaces JPA
├── security/             # JWT y filtros
├── service/              # Lógica de negocio
├── integration/          # Tests de integración reales
└── test/                 # Tests unitarios
```

---

## 📌 Roadmap (Fase 1 completada ✅)

- [x] Registro y login funcional
- [x] Seguridad JWT (HS256)
- [x] Dockerizado (DB + backend)
- [x] Swagger operativo
- [x] Manejo de errores avanzado
- [x] Tests unitarios + integración
- [x] Logging y serialización configurada

---

## 📬 Contacto

Proyecto educativo y técnico mantenido por **Andres**.  
En transición de QA a QA Engineer + Pentester.  
Mentoría y documentación guiada con enfoque profesional.

---

## 🧠 Notas Profesionales

- El sistema está diseñado para ser **portable, robusto y plug & play**.
- No requiere configuración manual fuera de Docker.
- Ideal para entornos de CI/CD, pruebas automatizadas y análisis estático.

---
