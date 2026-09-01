# Dockerfile for Media Management System
FROM eclipse-temurin:17-jdk-alpine as build

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create uploads directory
RUN mkdir -p /app/uploads

# Copy the built jar file
COPY --from=build /app/target/media-management-system-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]