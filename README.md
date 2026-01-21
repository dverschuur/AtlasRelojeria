# Atlas Relojería

Sistema de gestión para la joyería y relojería "Atlas", desarrollado con una arquitectura de microservicios separando el Backend del Frontend.

## 🚀 Tecnologías Utilizadas

Este proyecto está dividido en dos partes principales:

### Backend ☕
- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.4.5
- **Gestión de dependencias:** Maven
- **Persistencia:** Archivos JSON (ubicados en `backend/src/main/resources/`)
- **Características:**
  - API RESTful para gestión de productos, ventas, perfiles y órdenes de compra.
  - No requiere base de datos externa (SQL/NoSQL) para funcionar en esta versión.

### Frontend 🎨
- **Framework:** Vue.js
- **Estructura:** Single Page Application (SPA)
- **Vistas principales:**
  - Catálogo de productos
  - Gestión de perfiles (Admin/Usuario)
  - Carrito y Órdenes de compra
  - Historial y Reportes de ventas

## 📂 Estructura del Proyecto

```
AtlasRelojeria/
├── backend/            # Código fuente del servidor Spring Boot
│   ├── src/main/java   # Controladores, Modelos y Lógica de negocio
│   └── src/main/resources # Configuración y 'Base de datos' JSON
└── frontend/           # Código fuente de la aplicación Vue.js
    ├── src/views       # Vistas de la interfaz de usuario
    └── public          # Assets estáticos
```

## 🛠️ Instalación y Ejecución

### Requisitos Previos
- Java JDK 21
- Node.js y npm

### 1. Ejecutar el Backend
Desde la carpeta `backend`:

```bash
cd backend
# Asegúrate de tener JAVA_HOME configurado a JDK 21
mvn spring-boot:run
```
El servidor iniciará en el puerto por defecto (usualmente `http://localhost:8080`).

### 2. Ejecutar el Frontend
Desde la carpeta `frontend`:

```bash
cd frontend
# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run serve
```
La aplicación web estará disponible en la URL que indique la consola (usualmente `http://localhost:8080` o `http://localhost:8081` si hay conflicto).

## 👥 Autores
- Equipo de Desarrollo Atlas Relojería
- Universidad Católica Andrés Bello (UCAB)
