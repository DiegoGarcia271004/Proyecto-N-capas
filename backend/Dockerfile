# ── Stage 1: build ──────────────────────────────────────────────────────────  
FROM maven:3.9-eclipse-temurin-21 AS builder  
WORKDIR /app  
  
# Copiar pom primero para aprovechar caché de capas — las dependencias solo  
# se re-descargan si cambia el pom, no al cambiar código fuente  
COPY WarehouseInventory/pom.xml .  
RUN mvn dependency:go-offline -q  
  
COPY WarehouseInventory/src ./src  
RUN mvn package -DskipTests -q  
  
# ── Stage 2: runtime ─────────────────────────────────────────────────────────  
FROM eclipse-temurin:21-jre-jammy  
WORKDIR /app  
  
# Usuario no-root para producción  
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser  
USER appuser  
  
COPY --from=builder /app/target/*.jar app.jar  
  
EXPOSE 8080  
  
ENTRYPOINT ["java", "-jar", "app.jar"]