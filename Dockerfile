# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /workspace/app

# Copia apenas o pom.xml primeiro
COPY pom.xml .

# Cache de dependências Maven
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src/ ./src/

# Build do projeto
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseStringDeduplication" \
    SPRING_PROFILES_ACTIVE="prod"

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup && \
    mkdir /app && \
    chown -R appuser:appgroup /app

WORKDIR /app

COPY --from=build --chown=appuser:appgroup /workspace/app/target/*.jar app.jar

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]