# Multi-stage Docker build for ShopMall Backend

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine

# Install dumb-init and wget for health check
RUN apk add --no-cache dumb-init wget

# Create app user
RUN addgroup -S shopmall && adduser -S shopmall -G shopmall

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/shopmall-backend-*.jar app.jar

# Change ownership
RUN chown -R shopmall:shopmall /app

# Switch to non-root user
USER shopmall

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Run application
ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "app.jar"]
