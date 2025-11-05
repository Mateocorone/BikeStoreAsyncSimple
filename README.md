# 🏍️ BikeStore Async v1.0 (Java – RabbitMQ)

## 📘 Descripción

Este proyecto implementa una **arquitectura asíncrona básica orientada a colas** usando **RabbitMQ** y **Java puro** (sin Spring Boot).  
Simula el proceso de **confirmación de pedidos**, **pago** y **envío de confirmación por correo** en una tienda de bicicletas (*BikeStore*).

### 🎯 Objetivo académico

Demostrar el funcionamiento de una **arquitectura orientada a servicios (SOA)** con mensajería asíncrona, manejo de fallos y cola de mensajes muertos (**Dead Letter Queue – DLQ**).

---

## ⚙️ Componentes principales

| Clase | Rol | Descripción |
|--------|-----|-------------|
| `Main.java` | Punto de entrada | Arranca los consumidores y envía los mensajes de prueba |
| `OrderProducer.java` | Productor | Publica los pedidos como JSON en la cola `order.queue` |
| `PaymentWorker.java` | Consumidor 1 | Procesa los pagos con un 50% de probabilidad de fallo |
| `EmailWorker.java` | Consumidor 2 | Envía confirmación solo si el pago fue exitoso |
| `OrderMessage.java` | Modelo | Estructura del mensaje JSON (pedidoId, email, total, forceFail) |
| `LogFmt.java` | Utilitario | Muestra logs con timestamp y nombre de hilo |

---

## 🧱 Requisitos previos

- **Java 17** o superior  
- **Gradle 8+**  
- **Docker Desktop** (para ejecutar RabbitMQ)

---
## 🚀 Instrucciones de ejecución

### 🐇 1. Levantar RabbitMQ con Docker

Ejecuta el siguiente comando en PowerShell o terminal para iniciar el contenedor con RabbitMQ y su panel de administración web:

###bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3.13-management

##Una vez iniciado, abre el panel de administración en tu navegador:

http://localhost:15672

Usuario: guest
Contraseña: guest


## ⚙️ Compilar el proyecto

Desde la raíz del repositorio, ejecuta:

```bash
./gradlew clean build

Ejecutar la aplicación

Ejecuta el proyecto directamente con el siguiente comando:

java -cp build/classes/java/main com.bikestore.Main

Si todo está configurado correctamente, verás en la consola un flujo similar a este:


[pedidoId=-] PaymentWorker -> escuchando en order.queue
[pedidoId=ORD-1001] OrderProducer -> enviado a order.queue
[pedidoId=ORD-1001] PaymentWorker -> PAID
[pedidoId=ORD-1001] EmailWorker -> enviado email a cliente@demo.com por $149.9
[pedidoId=ORD-FAIL] OrderProducer -> enviado a order.queue
[pedidoId=ORD-FAIL] PaymentWorker -> FAIL intento 1
[pedidoId=ORD-FAIL] PaymentWorker -> FAIL intento 2
[pedidoId=ORD-FAIL] PaymentWorker -> FAIL intento 3
[pedidoId=ORD-FAIL] PaymentWorker -> enviado a DLQ


Resultado esperado

El proyecto simula correctamente la comunicación asíncrona entre un productor y dos consumidores,
mostrando logs en consola y manejando reintentos automáticos con envío a la cola de errores (DLQ) cuando corresponde.
