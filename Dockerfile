# Use official OpenJDK 17 image as base
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Spring Boot application JAR file into the container
COPY target/namada-me-1.0.0.jar /app/namada-me-1.0.0.jar

# Expose port 8282
EXPOSE 8282

# Command to run the Spring Boot application
CMD ["java", "-jar", "namada-me-1.0.0.jar"]
