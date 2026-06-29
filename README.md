# Proyecto-N-capas

## Definición de proyecto
Dada la temática, realice un sistema backend y frontend, conectados por una API,
cumpliendo las reglas de negocio definidas usando los roles definidos.
Cada proyecto debe cumplir con:
• Lógica de negocios definida
• Manejo de excepciones
• Uso correcto de codigos de respuesta HTTP
• Uso correcto de métodos HTTP
• Manejo de autorizacion y autenticacion
• Manejo correcto de la arquitectura que use el proyecto.
• Despliegue en la nube

## Puntaje Extra
Realice una integración con la plataforma facilitadora de pagos en línea Stripe para el
pago de servicios requeridos en su proyecto (e.g: comprar el curso, pagar el producto,
etc.)
De no realizarse esta integración, simplemente simule un formulario de pago

## Entregables
• Repositorio tanto para frontend como para backend
• Documentación de la API
• Diagrama Entidad-Relación de la base de datos
• URL del sistema desplegado en la nube
• Reporte general de los aportes individuales al proyecto
• Video guía del despliegue de su proyecto en la nube

**Temática**
Gestión de productos, lotes, ubicaciones y órdenes de pedido.

**Lógica de negocio**
1. Catálogo de productos con SKU, peso, dimensiones.
2. Control de lotes por fecha de caducidad (FIFO).
3. Ubicación en almacén (rack, pasillo, nivel).
4. Entradas de inventario (compra a proveedor).
5. Salidas (órdenes de clientes o transferencias).
6. Alerta automática por stock mínimo.
7. Reabastecimiento sugerido (ROP).
8. Múltiples almacenes con traslados.
9. Conteo cíclico (auditoría).
10.Reserva de inventario para órdenes no pagadas (timeout).
11.Historial de movimientos por lote.
12.Reporte de rotación de inventario (ABC).
13.Integración con proveedores (API).
14.Escaneo de códigos de barra / RFID.
15.Costo promedio ponderado por producto.
   
**Roles**
• Administrador: configura almacenes, políticas, ve auditoría global.
• Jefe de almacén: gestiona inventario, ordena traslados, revisa alertas.
• Operario: registra entradas/salidas, escanea códigos.

**Desafío de escalabilidad**
El método de asignación de ubicación para productos nuevos varía según la estación del
año (navidad: ubicación cerca de zona de empaque) o por tipo de producto (congelados
cerca de cámaras).
Problema: Escalar a 10 almacenes con millones de SKU, donde cada almacén puede usar
una política de asignación diferente (por ejemplo, azar, llenado por pasillo, cercanía a
salida). Un algoritmo rígido obliga a reprogramar cada cambio.


---

Esta documentación describe en detalle la arquitectura, el flujo de datos, la gestión de estados y los endpoints integrados en la aplicación frontend del Warehouse Management System (WMS).

1. Arquitectura y Estructura del Proyecto

El frontend está desarrollado sobre React 18 y TypeScript, utilizando Vite como servidor de desarrollo y empaquetador de producción. El diseño visual se basa en CSS Vanilla para mayor velocidad y control de carga.

Estructura de Directorios

bash

wms-frontend/

├── src/

│   ├── components/            
│   │   ├── Layout/           

│   │   └── Dashboard/        

│   ├── context/              

│   ├── core/                  

│   │   └── api/               
│   ├── features/             

│   │   ├── auth/              

│   │   ├── catalog/          

│   │   ├── inventory/        

│   │   └── operations/        

(ScanSimulation, TareaConteo)

│   ├── mock/                  

│   ├── App.tsx                

│   └── main.tsx               

├── package.json               

└── .env.local                 

2. Gestión de Estado Global (WmsContext.tsx)

La aplicación implementa el patrón React Context API mediante el proveedor WmsProvider. Este contexto centraliza el estado de la aplicación, evitando el traspaso ineficiente de propiedades (prop drilling).

Funcionalidades del Contexto:

• Sesión del Usuario: Almacena la información del usuario autenticado (UserSession) y persiste la sesión usando localStorage.

• Carga de Datos: Realiza consultas automáticas al backend de productos (fetchSkus), notificaciones activas (fetchNotifications) e infraestructura de almacenes (fetchInfrastructure).

• Respaldo Automático (Fallback Offline): En caso de que el backend remoto esté apagado o tenga errores de red, cada petición del contexto está protegida por un bloque try-catch que conmuta automáticamente al estado local en memoria (mockData.ts).

3. Integración de la API (Axios Client)

Las peticiones HTTP están centralizadas en apiClient.ts utilizando la biblioteca Axios.

• Manejo de Sesión Compartida: La propiedad withCredentials: true asegura que las cookies HTTP-Only de Spring Security (JSESSIONID) se envíen y reciban automáticamente en cada petición cross-origin (CORS).

4. Endpoints y Peticiones HTTP Integradas

El frontend tiene mapeadas e implementadas las peticiones a la API del backendcorrespondientes a los 6 módulos funcionales solicitados:

Módulo 1: Autenticación y Acceso (Auth)

• POST /api/auth/login: Valida credenciales e inicializa la cookie de sesión.

• POST /api/auth/logout: Cierra la sesión activa en el servidor y limpia localStorage.

• POST /api/auth/register: Registra un nuevo usuario en la base de datos (función registerUser).

Módulo 2: Catálogo Maestro (Productos)

• GET /api/product/inactive: Carga el catálogo completo de productos del almacén incluyendo SKUs activos e inactivos.

Módulo 3: Infraestructura (Bodegas y Ubicaciones)

• GET /api/warehouse: Solicita la lista de bodegas disponibles.

• POST /api/warehouse: Crea una nueva bodega (createWarehouse).

• PUT /api/warehouse/{id}: Modifica datos de una bodega (updateWarehouse).

• DELETE /api/warehouse/{id}: Desactiva una bodega (deleteWarehouse).

• PATCH /api/warehouse/{id}/activate: Reactiva una bodega (activateWarehouse).

• PUT /api/warehouse-policy/{warehouse}: Modifica la estrategia espacial de asignación de lotes (Aleatorio o Fijo por Familia).

• GET /api/warehouse-policy/{warehouse}: Obtiene la estrategia espacial de la bodega.

• POST /api/storage-location: Añade una ubicación física (createStorageLocation).

• GET /api/storage-location/warehouse/{warehouse}: Lista todas las posiciones de un almacén.

Módulo 4: Operaciones Diarias (Inventario y Reservas)

• POST /api/inventory/entry: Registra ingresos de mercancía por scanner.

• POST /api/inventory/consume: Registra consumos de mercancía bajo regla FIFO.

• POST /api/reservation: Registra una reserva temporal de stock por 10 minutos.

• PUT /api/reservation/confirm/{id}: Confirma una reserva activa convirtiéndola en salida definitiva.

• PUT /api/reservation/release/{id}: Cancela una reserva liberando el stock a la bodega.

Módulo 5: Auditoría y Reabastecimiento

• POST /api/cyclic-counts: Registra un nuevo conteo físico sobre un lote.

• PATCH /api/cyclic-counts/{id}/start: Activa el inicio del conteo por el operario.

• PATCH /api/cyclic-counts/{id}/submit: Envía el conteo físico real para calcular discrepancias.

• GET /api/cyclic-counts: Consulta el historial de conteos y auditorías físicas.

• PATCH /api/reorder-suggestions/{id}/attended: Marca la sugerencia de compra (ROP) como resuelta tras aprobar la compra.

Módulo 6: Reportes y Notificaciones

• GET /api/reporting/abc: Genera el reporte ABC de rotación de SKUs por rango de fechas.

• GET /api/notifications/unread: Obtiene las alertas de stock crítico no leídas en el sistema.

5. Lógica de Negocio Crítica del Frontend

1. Despacho FIFO (First In, First Out)

Al procesar una salida de stock por scanner (confirmScan), el sistema busca los lotes activos del SKU, los ordena ascendentemente por su propiedad fechaCaducidad y va restando la cantidad solicitada del lote más antiguo. Si un lote no es suficiente, consume el saldo del lote que le sigue en antigüedad de caducidad.

2. Guardas de Enrutamiento y Roles

El archivo App.tsx define rutas protegidas (ProtectedRoute) según el rol guardado en la sesión:

• Administradores: Tienen acceso al módulo de configuración de políticas y bodegas (/configuracion-espacial).

• Jefes de Almacén: Acceso a reportes ABC, alertas ROP, creación de reservas y auditorías (/dashboard-analitico, /movimientos, /auditorias).

• Operarios: Acceso a la terminal de simulación de escaneo y tareas de conteo cíclico a ciegas (/terminal-escaner, /tarea-conteo).

6. Comandos de Operación Local

Para ejecutar o compilar el frontend en el entorno local , nos debemos de situar en la carpeta wms-frontend/ y usar los siguientes comandos:

npm run dev

Compilación para Producción

npm run build

Verificación de Tipos (TypeScript)

Verifica que no existan errores de código o de definición de tipos en la aplicación:

bash

npx tsc --noEmit

---

# WarehouseInventory

Backend Spring Boot con JWT en cookie, CSRF, roles y gestión de inventario para almacenes.

📌 Resumen del proyecto

WarehouseInventory es una API REST modular para la operación de un sistema de almacenes. La aplicación cubre autenticación, gestión de productos, control de inventario, reservas, bodegas, ubicaciones de almacenamiento y reportes operativos.

El proyecto está diseñado para operar con PostgreSQL y utiliza JWT firmado con RSA en `access_token` cookie, junto a protección CSRF basada en cookies.

🧩 Tecnologías principales

- Java 21
- Spring Boot 4.0.6
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Web MVC
- Springdoc OpenAPI
- PostgreSQL
- Lombok
- Nimbus JWT

📁 Estructura importante

- `src/main/java/org/example/warehouseinventory/WarehouseInventoryApplication.java` - punto de entrada de la app.
- `src/main/java/org/example/warehouseinventory/auth/` - autenticación, login, logout y registro.
- `src/main/java/org/example/warehouseinventory/catalog/` - catálogo de productos.
- `src/main/java/org/example/warehouseinventory/inventory/` - registro de ingresos y consumo de stock.
- `src/main/java/org/example/warehouseinventory/order/` - reservas de inventario.
- `src/main/java/org/example/warehouseinventory/warehouse/` - bodegas, ubicaciones y políticas de asignación.
- `src/main/java/org/example/warehouseinventory/reporting/` - reportes ABC, sugerencias de reorden y notificaciones.
- `src/main/java/org/example/warehouseinventory/shared/` - respuestas generales, excepciones, utilidades y configuración compartida.
- `src/main/resources/application.yaml` - configuración principal.

🔒 Diseño de seguridad

Seguridad general

- `SecurityConfig` habilita seguridad sin estado (`STATELESS`) y desactiva login por formulario.
- La autorización se define por roles en los controladores con `@PreAuthorize`.
- Se usan JWT dentro de la cookie `access_token`.
- El filtro `cookieTokenFilter()` convierte la cookie en el header `Authorization: Bearer <token>` para Spring Security.
- Se aceptan sin autenticación: `/api/auth/login`, `/api/auth/logout`, `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`.
- El resto de rutas requiere autenticación.

Autenticación JWT

- `JwtService` genera tokens con una clave RSA generada al arranque.
- El token incluye el `subject` con el username y el claim `role`.
- Al iniciar sesión, el token se envía al cliente en la cookie `access_token`.
- También existe `CsrfTokenRepository` basado en cookies para soportar CSRF.

Roles

- El sistema define roles como `ADMIN`, `WAREHOUSE_MANAGER`, `OPERATOR`.
- El `JwtAuthenticationConverter` asigna la autoridad `ROLE_<ROL>` desde el claim `role`.
- Los endpoints usan `@PreAuthorize` para restringir acceso según rol.

🧠 Comportamiento de CSRF

- CSRF está habilitado por defecto (`app.csrf-enabled=true`).
- Se utiliza `CookieCsrfTokenRepository.withHttpOnlyFalse()`.
- `/api/auth/login` y `/api/auth/logout` están excluidos de la verificación CSRF.

🧾 Modelo de datos principal

El proyecto tiene entidades para:

- `User` - usuario con `username`, `password` cifrado (BCrypt), `role` y `active`.
- `Product` - productos del catálogo con SKU, categoría y estado activo/inactivo.
- `Warehouse` - almacenes físicos.
- `StorageLocation` - ubicaciones dentro de un almacén.
- `Reservation` - reservas de inventario.
- `CyclicCount` - controles cíclicos de inventario.
- `ReorderSuggestion` / `Notification` - sugerencias de reorden y notificaciones operativas.

📦 Endpoints disponibles

### Auth

- `POST /api/auth/login`
  - body: `{ "username": "...", "password": "..." }`
  - devuelve cookie `access_token` y token CSRF.
- `POST /api/auth/logout`
  - cierra sesión y borra cookie.
- `POST /api/auth/register`
  - requiere rol `ADMIN`.
  - registra nuevo usuario.

### Catálogo de productos

- `POST /api/product` - crear producto.
- `GET /api/product/id/{id}` - obtener producto por ID.
- `GET /api/product/sku/{sku}` - obtener producto por SKU.
- `GET /api/product/category/{category}` - filtrado por categoría.
- `GET /api/product/inactive` - obtener productos inactivos.
- `GET /api/product/id/inactive/{id}` - producto inactivo por ID.
- `PUT /api/product/update/{id}` - actualizar producto.
- `DELETE /api/product/delete/{id}` - desactivar producto.
- `PUT /api/product/activate/{id}` - activar producto.

### Inventario

- `POST /api/inventory/entry` - registrar ingreso de inventario.
- `POST /api/inventory/consume` - consumir stock.

### Almacenes y ubicaciones

- `POST /api/warehouse` - crear almacén.
- `GET /api/warehouse` - listar almacenes.
- `GET /api/warehouse/{id}` - obtener almacén por ID.
- `PUT /api/warehouse/{id}` - actualizar almacén.
- `DELETE /api/warehouse/{id}` - desactivar almacén.
- `PATCH /api/warehouse/{id}/activate` - activar almacén.
- `POST /api/storage-location` - crear ubicación de almacenamiento.
- `GET /api/storage-location/warehouse/{warehouse}` - ubicaciones por almacén.
- `GET /api/storage-location/{id}` - ubicar por ID.

### Políticas de almacén

- `GET /api/warehouse-policy/{warehouse}` - obtener política de asignación.
- `PUT /api/warehouse-policy/{warehouse}?strategy=<strategy>` - actualizar estrategia de asignación.

### Reservas

- `POST /api/reservation` - crear reserva.
- `PUT /api/reservation/confirm/{id}` - confirmar reserva.
- `PUT /api/reservation/release/{id}` - liberar reserva.

### Reporting

- `GET /api/reporting/abc?from=<YYYY-MM-DD>&to=<YYYY-MM-DD>` - generar reporte ABC.
- `GET /api/reorder-suggestions` - obtener todas las sugerencias de reorden.
- `GET /api/reorder-suggestions/pending` - sugerencias pendientes.
- `PATCH /api/reorder-suggestions/{id}/attended` - marcar sugerencia como atendida.

### Notificaciones

- `GET /api/notifications` - obtener todas las notificaciones.
- `GET /api/notifications/unread` - notificaciones no leídas.
- `PATCH /api/notifications/{id}/read` - marcar notificación como leída.
- `GET /api/notifications/active` - notificaciones activas.

### Conteos cíclicos

- `POST /api/cyclic-counts` - crear conteo cíclico.
- `PATCH /api/cyclic-counts/{id}/start` - iniciar conteo.
- `PATCH /api/cyclic-counts/{id}/submit` - enviar conteo físico.
- `GET /api/cyclic-counts` - listar todos los conteos.
- `GET /api/cyclic-counts/status/{status}` - filtrar por estado.

⚙️ DTOs y respuestas

El proyecto usa DTOs de petición y respuesta en cada módulo para validar datos y homogeneizar las respuestas.

- `LoginRequest`, `RegisterRequest`
- `CreateProductRequest`, `UpdateProductRequest`
- `InventoryEntryRequest`, `StockConsumptionRequest`
- `CreateWarehouseRequest`, `UpdateWarehouseRequest`
- `CreateStorageLocationRequest`
- `ReservationRequest`
- `CreateCyclicCountRequest`, `SubmitPhysicalCountRequest`
- `GeneralResponse` - respuesta estándar de la API.

📦 Variables de entorno usadas

- `DB_URL` - URL de conexión PostgreSQL.
- `DB_USER` - usuario de la base de datos.
- `DB_PASSWORD` - contraseña de la base de datos.
- `SPRING_PROFILES_ACTIVE` - perfil activo (`dev`, `prod`).
- `SERVER_PORT` - puerto de la aplicación.
- `app.csrf-enabled` - habilita/deshabilita CSRF en `SecurityConfig`.

🚀 Ejecución local

```bash
./mvnw clean package
./mvnw spring-boot:run
```

O con Maven instalado:

```bash
mvn clean package
mvn spring-boot:run
```

🐳 Docker

Este repositorio no incluye `Dockerfile` ni `docker-compose.yml` por defecto.

📄 Swagger / OpenAPI

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

🧪 Pruebas

```bash
./mvnw test
```

✅ Observaciones del código

- El token JWT se guarda en cookie `access_token` y se lee con un filtro personalizado antes de la autenticación.
- `SecurityConfig` convierte la cookie en encabezado `Authorization` y valida JWT con RSA.
- CSRF se maneja con `CookieCsrfTokenRepository` y se ignoran las rutas de login/logout.
- `WarehouseInventoryApplication` crea un admin por defecto en perfiles distintos a `test`.
- `spring.jpa.hibernate.ddl-auto=update` sincroniza el esquema automáticamente en desarrollo.

📝 Recomendaciones

- Crea `.env` o define variables de entorno antes de ejecutar.
- Verifica la conexión PostgreSQL con `DB_URL`, `DB_USER` y `DB_PASSWORD`.
- Prueba primero `/api/auth/login` y luego los endpoints protegidos.
- Usa Swagger para explorar rutas y payloads.