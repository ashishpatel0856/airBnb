# Use official Java 17 image
FROM eclipse-temurin:17-jdk

#  working directory inside container
WORKDIR /app

# Copy project files
COPY . .

# Ensure Maven wrapper is executable (Windows fix)
RUN chmod +x mvnw

# Build the app without tests
RUN ./mvnw clean package -DskipTests

# Expose the port Spring Boot runs on
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "target/airBnbApp-0.0.1-SNAPSHOT.jar"]
