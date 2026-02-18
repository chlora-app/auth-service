# ===============================
# Stage 1 — Build
# ===============================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom & download dependencies first (cache layer)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn -B clean package -DskipTests


# ===============================
# Stage 2 — Runtime
# ===============================
FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring default port
EXPOSE 8080

# JVM tuning for container
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
