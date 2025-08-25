# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /workspace/app

# Instala wget para healthcheck
RUN apk add --no-cache wget

# Copia apenas o pom.xml primeiro para otimizar cache
COPY pom.xml .

# Cache de dependências Maven
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src/ ./src/

# Build do projeto (sem testes para acelerar)
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Instala wget para healthcheck
RUN apk add --no-cache wget

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE="prod" \
    TZ="America/Sao_Paulo"

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup && \
    mkdir /app && \
    chown -R appuser:appgroup /app

WORKDIR /app

# Copia o jar do build stage
COPY --from=build --chown=appuser:appgroup /workspace/app/target/*.jar app.jar

USER appuser

EXPOSE 8080

# Healthcheck mais robusto
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]