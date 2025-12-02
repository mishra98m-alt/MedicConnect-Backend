# ===========================
# Stage 1: Build MedicConnect
# ===========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy parent pom.xml
COPY pom.xml .

# Copy full modules directory (including core)
COPY modules modules

# Pre-download dependencies (speeds up builds)
RUN mvn -B dependency:go-offline

# Build only the core module
RUN mvn -B -pl modules/core -am clean package -DskipTests

# ===========================
# Stage 2: Runtime image
# ===========================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the final jar from builder
COPY --from=builder /build/modules/core/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
