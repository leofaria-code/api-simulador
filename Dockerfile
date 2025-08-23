# Build stage
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /workspace/app

# Copiar apenas o POM primeiro para cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fonte e construir
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Variáveis de ambiente para JVM
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Criar usuário não-root
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copiar apenas o JAR necessário
COPY --from=build /workspace/app/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]