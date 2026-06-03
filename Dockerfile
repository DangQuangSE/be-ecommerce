# Multi-stage build for local Docker Compose (clone → docker compose up).
# Production CI still uses Dockerfile.runtime + pre-built JAR (deploy-vm.yml).

FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline -Dmaven.test.skip=true
COPY src ./src
RUN mvn -B clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx600m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
