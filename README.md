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
